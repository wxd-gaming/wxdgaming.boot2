package wxdgaming.logserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlConfiguration;
import wxdgaming.boot2.starter.scheduled.ScheduledProperties;

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
                PgsqlConfiguration.class,
                ScheduledProperties.class,
                LogServerApplication.class
        }
)
public class LogServerApplication {


    public static void main(String[] args) {
        log.info("日志中心启动中...");
        try {
            new SpringApplicationBuilder(LogServerApplication.class).run(args);
            SpringUtil.mainApplicationContextProvider
                    .executeMethodWithAnnotatedInit()
                    .startBootstrap()
                    .addShutdownHook();
            log.info("日志中心启动完成...");
        } catch (Exception e) {
            log.debug("日志中心启动异常...", e);
            System.exit(99);
        }
    }

}
