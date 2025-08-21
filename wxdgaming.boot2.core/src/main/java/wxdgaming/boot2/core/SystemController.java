package wxdgaming.boot2.core;

import ch.qos.logback.core.LogbackUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统函数
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-08 11:35
 **/
@Slf4j
@RestController
@RequestMapping("/system/spi")
public class SystemController {

    @RequestMapping("/logLv")
    public String logLv() {
        return LogbackUtil.refreshLoggerLevel();
    }

}
