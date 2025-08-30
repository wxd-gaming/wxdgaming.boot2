package wxdgaming.game.login;

import ch.qos.logback.core.LogbackUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.util.JvmUtil;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlConfiguration;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientConfiguration;
import wxdgaming.boot2.starter.scheduled.ScheduledConfiguration;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.game.basic.slog.SlogService;
import wxdgaming.logbus.LogBusService;

/**
 * 登录
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-27 20:56
 **/
@Slf4j
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreScan.class,
                HttpClientConfiguration.class,
                ScheduledConfiguration.class,
                PgsqlConfiguration.class,
                SlogService.class,
                LogBusService.class,
                LoginServerApplication.class
        }
)
public class LoginServerApplication {

    public static void main(String[] args) {
        try {
            new SpringApplicationBuilder(LoginServerApplication.class).run(args);
            SpringUtil.mainApplicationContextProvider
                    .executeMethodWithAnnotatedInit()
                    .startBootstrap()
                    .addShutdownHook();
        } catch (Exception e) {
            log.error("登录服务启动异常...", e);
            JvmUtil.halt(99);
        }
    }

}
