package wxdgaming.game.gateway;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.net.SocketConfiguration;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientConfiguration;
import wxdgaming.boot2.starter.scheduled.ScheduledProperties;
import wxdgaming.boot2.core.MainApplicationContextProvider;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.game.basic.login.LoginProperties;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-27 20:56
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreScan.class,
                LoginProperties.class,
                SocketConfiguration.class,
                HttpClientConfiguration.class,
                ScheduledProperties.class,
                GatewayApplication.class
        }
)
public class GatewayApplication {

    public static void main(String[] args) {

        MainApplicationContextProvider.builder(GatewayApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        SpringUtil.mainApplicationContextProvider
                .executeMethodWithAnnotatedInit()
                .startBootstrap()
                .addShutdownHook();

    }

}
