package wxdgaming.logserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import wxdgaming.boot2.core.ApplicationStartBuilder;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;
import wxdgaming.game.authority.AuthorityScan;

/**
 * 日子中心启动器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 15:02
 **/
@Slf4j
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreScan.class,
                PgsqlScan.class,
                ScheduledScan.class,
                AuthorityScan.class,
                LogServerApplication.class
        }
)
public class LogServerApplication {


    public static void main(String[] args) {
        log.info("日志中心启动中...");
        try {
            ApplicationStartBuilder.builder(LogServerApplication.class)
                    .run(args)
                    .postInitEvent()
                    .startBootstrap();
            log.info("日志中心启动完成...");
        } catch (Exception e) {
            log.debug("日志中心启动异常...", e);
            System.exit(99);
        }
    }

}
