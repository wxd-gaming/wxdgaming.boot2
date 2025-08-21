package wxdgaming.logbus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.SpringUtil;

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
                LogBusApplication.class
        }
)
public class LogBusApplication {

    public static void main(String[] args) {
        log.info("LogBus...");
        try {
            new SpringApplicationBuilder(LogBusApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);

            SpringUtil.mainApplicationContextProvider
                    .executeMethodWithAnnotatedInit()
                    .startBootstrap()
                    .addShutdownHook();

            log.info("LogBus启动完成...");
        } catch (Exception e) {
            log.debug("LogBus启动异常...", e);
            System.exit(99);
        }
    }

}
