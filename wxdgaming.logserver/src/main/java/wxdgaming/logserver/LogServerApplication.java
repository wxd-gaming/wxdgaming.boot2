package wxdgaming.logserver;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.RunApplicationMain;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan;
import wxdgaming.boot2.starter.net.SocketScan;
import wxdgaming.boot2.starter.scheduled.ScheduledProperties;

/**
 * 日子中心启动器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 15:02
 **/
@Slf4j
public class LogServerApplication {


    public static void main(String[] args) {
        log.info("日志中心启动中...");
        try {
            RunApplicationMain runApplication = WxdApplication.run(
                    CoreScan.class,
                    PgsqlScan.class,
                    ScheduledProperties.class,
                    SocketScan.class,
                    LogServerApplication.class
            );
            runApplication.start();
            log.info("日志中心启动完成...");
        } catch (Exception e) {
            log.debug("日志中心启动异常...", e);
            System.exit(99);
        }
    }

}
