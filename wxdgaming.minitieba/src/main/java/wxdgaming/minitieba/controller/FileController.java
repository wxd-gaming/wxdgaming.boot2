package wxdgaming.minitieba.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.minitieba.service.FileService;

/**
 * 文件上传API
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@RestController
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /** 上传文件 */
    @RequestMapping("/upload")
    public RunResult upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return RunResult.fail("文件不能为空");
        }
        try {
            String url = fileService.saveFile(file.getBytes(), file.getOriginalFilename());
            return RunResult.ok().data(url);
        } catch (Exception e) {
            return RunResult.fail("文件上传失败: " + e.getMessage());
        }
    }

}
