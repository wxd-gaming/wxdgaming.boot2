package wxdgaming.minitieba.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.minitieba.service.FileService;


/**
 * 文件上传API - 支持图片压缩、视频上传，数据存储到 RocksDB
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;

    /** 浏览器缓存时间（秒）- 1天 */
    private static final int CACHE_MAX_AGE = 86400;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /** 上传文件（自动识别图片或视频） */
    @RequestMapping("/upload")
    public RunResult upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return RunResult.fail("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = file.getName();
        }

        String ext = getExtension(originalFilename).toLowerCase();
        String fileKey;

        try {
            byte[] data = file.getBytes();

            // 根据扩展名判断是图片还是视频
            if (isImageExt(ext)) {
                fileKey = fileService.saveImage(data, originalFilename);
            } else if (isVideoExt(ext)) {
                fileKey = fileService.saveVideo(data, originalFilename);
            } else {
                return RunResult.fail("不支持的文件格式，仅支持图片和视频");
            }

            if (fileKey == null) {
                return RunResult.fail("文件保存失败");
            }

            return RunResult.ok().data(fileKey);

        } catch (Exception e) {
            log.error("文件上传失败", e);
            return RunResult.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件 - 支持浏览器缓存
     */
    @RequestMapping("/get/{fileKey}")
    public void getFile(
            @PathVariable String fileKey,
            HttpServletRequest request,
            HttpServletResponse response) {

        log.info("获取文件请求: fileKey={}", fileKey);
        
        byte[] data = fileService.getFile(fileKey);
        if (data == null || data.length == 0) {
            log.warn("文件不存在: fileKey={}", fileKey);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        log.info("文件获取成功: fileKey={}, size={}KB", fileKey, data.length / 1024);

        String contentType = fileService.getContentType(fileKey);
        log.info("Content-Type: {}", contentType);

        // 设置响应头
        response.setContentType(contentType);
        response.setContentLength(data.length);

        // 设置缓存控制 - 公开缓存，缓存1天
        response.setHeader("Cache-Control", "public, max-age=" + CACHE_MAX_AGE);
        response.setHeader("Pragma", "cache");
        response.setDateHeader("Expires", System.currentTimeMillis() + CACHE_MAX_AGE * 1000L);

        // 设置 ETag 用于条件请求
        String etag = "\"" + Integer.toHexString((fileKey + data.length).hashCode()) + "\"";
        response.setHeader("ETag", etag);

        // 处理条件请求（If-None-Match）
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (etag.equals(ifNoneMatch)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        // 设置 Last-Modified
        response.setDateHeader("Last-Modified", System.currentTimeMillis());

        // 输出数据
        try {
            response.getOutputStream().write(data);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("文件输出失败: {}", fileKey, e);
        }
    }

    /**
     * 获取文件信息（不返回数据）
     */
    @GetMapping("/info/{fileKey}")
    public RunResult getFileInfo(@PathVariable String fileKey) {
        byte[] data = fileService.getFile(fileKey);
        if (data == null || data.length == 0) {
            return RunResult.fail("文件不存在");
        }
        String contentType = fileService.getContentType(fileKey);
        return RunResult.ok().fluentPut("key", fileKey)
                .fluentPut("contentType", contentType)
                .fluentPut("size", data.length);
    }

    private String getExtension(String fileName) {
        if (fileName == null) return "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            return fileName.substring(dotIndex);
        }
        return "";
    }

    private boolean isImageExt(String ext) {
        return ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") ||
                ext.equals(".gif") || ext.equals(".webp") || ext.equals(".bmp");
    }

    private boolean isVideoExt(String ext) {
        return ext.equals(".mp4") || ext.equals(".webm") || ext.equals(".ogg") ||
                ext.equals(".mov") || ext.equals(".avi");
    }
}
