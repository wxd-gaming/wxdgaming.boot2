package wxdgaming.game.login;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlConfiguration;
import wxdgaming.boot2.starter.net.SocketConfiguration;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientConfiguration;
import wxdgaming.boot2.starter.scheduled.ScheduledProperties;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.game.basic.slog.SlogService;
import wxdgaming.logbus.LogBusService;

/**
 * 登录
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-27 20:56
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreScan.class,
                HttpClientConfiguration.class,
                ScheduledProperties.class,
                PgsqlConfiguration.class,
                SlogService.class,
                LogBusService.class,
                LoginServerApplication.class
        }
)
public class LoginServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(LoginServerApplication.class).run(args);
        SpringUtil.mainApplicationContextProvider
                .executeMethodWithAnnotatedInit()
                .startBootstrap()
                .addShutdownHook();
    }

}
