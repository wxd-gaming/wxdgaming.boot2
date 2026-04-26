package wxdgaming.minitieba.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Slf4j
@Service
public class FileService {

    private static final String UPLOAD_DIR = "upload";
    private static final String[] ALLOWED_IMAGE_EXT = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    private static final String[] ALLOWED_VIDEO_EXT = {".mp4", ".webm", ".ogg"};

    public FileService() {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /** 保存文件并返回访问URL */
    public String saveFile(byte[] data, String originalFileName) {
        String ext = getExtension(originalFileName);
        String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;
        Path filePath = Paths.get(UPLOAD_DIR, newFileName);
        try {
            Files.write(filePath, data);
            log.info("文件保存成功: {}", filePath);
        } catch (IOException e) {
            log.error("文件保存失败", e);
            return null;
        }
        return "/upload/" + newFileName;
    }

    private String getExtension(String fileName) {
        if (fileName == null) return ".bin";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            return fileName.substring(dotIndex).toLowerCase();
        }
        return ".bin";
    }

}
