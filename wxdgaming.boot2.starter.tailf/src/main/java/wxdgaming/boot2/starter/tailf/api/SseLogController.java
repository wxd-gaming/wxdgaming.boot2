package wxdgaming.boot2.starter.tailf.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.util.NumberUtil;
import wxdgaming.boot2.starter.tailf.LastAccessFile;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 日志推送
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-08 14:06
 **/
@Slf4j
@Controller
@RequestMapping("/log")
public class SseLogController implements InitPrint {

    public SseLogController() {
    }

    @GetMapping("/index")
    public String index() {
        return "redirect:/log/log-tail-show.html";
    }

    @GetMapping("/sse")
    public SseEmitter sse(HttpServletRequest request) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        AtomicReference<LastAccessFile> lastAccessFile = new AtomicReference<>();
        String fileName = request.getParameter("fileName");
        int lines = NumberUtil.parseInt(request.getParameter("lastLines"), 10);
        if (lines < 1 || lines > 1000) {
            lines = 10;
        }
        lastAccessFile.set(new LastAccessFile("target/logs/" + fileName, lines, (line) -> {
            try {
                emitter.send(SseEmitter.event().name("log").data(line));
            } catch (IOException e) {
                // 客户端断开连接是正常现象，不需要记录为错误日志
                if (!e.getMessage().contains("中止了一个已建立的连接") &&
                    !e.getMessage().contains("Broken pipe")) {
                    log.error("Error sending error message", e);
                }
                // 关闭资源
                lastAccessFile.get().close();
            }
        }));

        // 连接完成或超时后的清理操作
        emitter.onCompletion(() -> {
            log.info("SSE connection completed");
            lastAccessFile.get().close();
        });

        emitter.onTimeout(() -> {
            log.info("SSE connection timeout");
            lastAccessFile.get().close();
        });

        emitter.onError((throwable) -> {
            log.warn("SSE connection error: {}", throwable.getMessage());
            lastAccessFile.get().close();
        });
        lastAccessFile.get().start();
        return emitter;
    }

}
