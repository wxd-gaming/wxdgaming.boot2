package wxdgaming.minitieba.service;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.starter.batis.rocksdb.RocksDBHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 文件服务 - 支持图片压缩、视频上传，数据存储到 RocksDB（LZ4压缩）
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Slf4j
@Service
public class FileService {

    private final RocksDBHelper db;
    private final RocksDB rawDb;

    private static final String FILE_PREFIX = "file:";
    private static final String FILE_ID_GENERATOR = "file_id_generator";

    /** 图片压缩后的最大宽度 */
    private static final int MAX_IMAGE_WIDTH = 1920;
    /** 图片压缩后的最大高度 */
    private static final int MAX_IMAGE_HEIGHT = 1080;
    /** 图片压缩质量 (0.0-1.0) */
    private static final float IMAGE_QUALITY = 0.85f;
    /** 图片最大文件大小，超过此大小则强制压缩（2MB） */
    private static final int MAX_IMAGE_SIZE = 2 * 1024 * 1024;
    /** 头像压缩尺寸（正方形） */
    private static final int AVATAR_SIZE = 200;
    /** 头像压缩质量 */
    private static final float AVATAR_QUALITY = 0.8f;

    /** 允许的图片扩展名 */
    private static final String[] ALLOWED_IMAGE_EXT = {".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"};
    /** 允许的视频扩展名 */
    private static final String[] ALLOWED_VIDEO_EXT = {".mp4", ".webm", ".ogg", ".mov", ".avi"};

    public FileService(RocksDBHelper db) {
        this.db = db;
        this.rawDb = db.getDb(); // 获取原生 RocksDB 实例
    }

    /** 生成文件ID */
    private synchronized long nextFileId() {
        long current = db.getLongValue(FILE_ID_GENERATOR);
        long next = current + 1;
        db.put(FILE_ID_GENERATOR, next);
        return next;
    }

    /**
     * 保存图片（自动压缩后存入 RocksDB）
     * @param data 原始图片数据
     * @param originalFileName 原始文件名
     * @return 文件ID，格式: img_{id}.jpg（统一输出为 JPEG）
     */
    public String saveImage(byte[] data, String originalFileName) {
        String ext = getExtension(originalFileName);
        if (!isAllowedImageExt(ext)) {
            log.warn("不支持的图片格式: {}", ext);
            return null;
        }

        try {
            // 压缩图片（统一输出为 JPEG）
            byte[] compressedData = compressImage(data, ext);
            if (compressedData == null || compressedData.length == 0) {
                log.warn("图片压缩失败，使用原图: {}", originalFileName);
                compressedData = data;
            }

            // 生成文件ID - 统一输出为 .jpg
            long fileId = nextFileId();
            String fileKey = "img_" + fileId + ".jpg";

            // 直接存入 RocksDB（不经过 Kryo 序列化）
            rawDb.put((FILE_PREFIX + fileKey).getBytes(StandardCharsets.UTF_8), compressedData);

            log.info("图片保存成功: id={}, 原始大小={}KB, 压缩后={}KB",
                    fileKey,
                    data.length / 1024,
                    compressedData.length / 1024);

            return fileKey;
        } catch (Exception e) {
            log.error("图片保存失败", e);
            return null;
        }
    }

    /**
     * 保存视频（直接存入 RocksDB）
     * @param data 原始视频数据
     * @param originalFileName 原始文件名
     * @return 文件ID，格式: vid_{id}
     */
    public String saveVideo(byte[] data, String originalFileName) {
        String ext = getExtension(originalFileName);
        if (!isAllowedVideoExt(ext)) {
            log.warn("不支持的视频格式: {}", ext);
            return null;
        }

        // 视频大小限制 100MB
        if (data.length > 100 * 1024 * 1024) {
            log.warn("视频文件过大: {}MB", data.length / 1024 / 1024);
            return null;
        }

        try {
            // 生成文件ID
            long fileId = nextFileId();
            String fileKey = "vid_" + fileId + ext;

            // 直接存入 RocksDB（不经过 Kryo 序列化）
            rawDb.put((FILE_PREFIX + fileKey).getBytes(StandardCharsets.UTF_8), data);

            log.info("视频保存成功: id={}, 大小={}MB", fileKey, data.length / 1024 / 1024);
            return fileKey;
        } catch (Exception e) {
            log.error("视频保存失败", e);
            return null;
        }
    }

    /**
     * 获取文件数据
     * @param fileKey 文件ID
     * @return 文件数据，如果不存在返回null
     */
    public byte[] getFile(String fileKey) {
        try {
            byte[] result = rawDb.get((FILE_PREFIX + fileKey).getBytes(StandardCharsets.UTF_8));
            if (result != null) {
                log.info("文件获取成功: fileKey={}, size={}KB", fileKey, result.length / 1024);
            }
            return result;
        } catch (RocksDBException e) {
            log.error("文件获取失败: fileKey={}", fileKey, e);
            return null;
        }
    }

    /**
     * 根据文件ID判断类型
     * 注意：所有图片统一存储为 JPEG 格式
     */
    public String getContentType(String fileKey) {
        if (fileKey == null) return "application/octet-stream";

        if (fileKey.startsWith("img_")) {
            // 所有图片统一输出为 JPEG
            return "image/jpeg";
        } else if (fileKey.startsWith("vid_")) {
            String ext = getExtension(fileKey);
            switch (ext.toLowerCase()) {
                case ".mp4":
                    return "video/mp4";
                case ".webm":
                    return "video/webm";
                case ".ogg":
                    return "video/ogg";
                case ".mov":
                    return "video/quicktime";
                case ".avi":
                    return "video/x-msvideo";
                default:
                    return "video/mp4";
            }
        }
        return "application/octet-stream";
    }

    /**
     * 压缩图片
     */
    private byte[] compressImage(byte[] imageData, String ext) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
            BufferedImage originalImage = ImageIO.read(bais);
            if (originalImage == null) {
                return null;
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // 计算缩放比例
            double widthRatio = (double) MAX_IMAGE_WIDTH / originalWidth;
            double heightRatio = (double) MAX_IMAGE_HEIGHT / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);

            int newWidth = originalWidth;
            int newHeight = originalHeight;

            // 如果图片超过限制尺寸或文件超过2MB，则进行压缩
            boolean needResize = originalWidth > MAX_IMAGE_WIDTH || originalHeight > MAX_IMAGE_HEIGHT;
            boolean needQualityCompress = imageData.length > MAX_IMAGE_SIZE;

            if (needResize) {
                newWidth = (int) (originalWidth * ratio);
                newHeight = (int) (originalHeight * ratio);
            }

            // 如果尺寸在限制内但文件过大，也需要调整尺寸
            if (!needResize && needQualityCompress) {
                // 按比例缩小直到文件小于限制
                double sizeRatio = Math.sqrt((double) MAX_IMAGE_SIZE / imageData.length);
                newWidth = (int) (originalWidth * sizeRatio);
                newHeight = (int) (originalHeight * sizeRatio);
                needResize = true;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Thumbnails.Builder<BufferedImage> builder = Thumbnails.of(originalImage);
            if (needResize) {
                builder.size(newWidth, newHeight);
            }
            builder.outputQuality(IMAGE_QUALITY).outputFormat("jpeg");

            builder.toOutputStream(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("图片压缩失败", e);
            return null;
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null) return ".bin";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            return fileName.substring(dotIndex).toLowerCase();
        }
        return ".bin";
    }

    private boolean isAllowedImageExt(String ext) {
        for (String allowed : ALLOWED_IMAGE_EXT) {
            if (allowed.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllowedVideoExt(String ext) {
        for (String allowed : ALLOWED_VIDEO_EXT) {
            if (allowed.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }
}
