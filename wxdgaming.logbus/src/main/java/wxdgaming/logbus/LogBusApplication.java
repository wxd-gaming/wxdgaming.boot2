package wxdgaming.logbus;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.RunApplicationMain;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientScan;

/**
 * 日子中心启动器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 15:02
 **/
@Slf4j
public class LogBusApplication {

    public static void main(String[] args) {
        log.info("LogBus...");
        try {
            RunApplicationMain runApplication = WxdApplication.run(
                    CoreScan.class,
                    HttpClientScan.class,
                    LogBusApplication.class
            );
            runApplication.start();
            log.info("LogBus启动完成...");
        } catch (Exception e) {
            log.debug("LogBus启动异常...", e);
            System.exit(99);
        }
    }

}
