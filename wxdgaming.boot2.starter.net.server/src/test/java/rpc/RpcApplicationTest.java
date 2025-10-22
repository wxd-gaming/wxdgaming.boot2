package rpc;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.MainApplicationContextProvider;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.starter.net.SocketScan;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-04 16:12
 **/
@EnableConfigurationProperties(TestProperties.class)
@SpringBootApplication(scanBasePackageClasses = {
        CoreScan.class,
        SocketScan.class,
        RpcApplicationTest.class,
})
public class RpcApplicationTest {

    public static void main(String[] args) throws InterruptedException {

        ConfigurableApplicationContext context = MainApplicationContextProvider.builder(RpcApplicationTest.class)
                .run(args);
        SpringUtil.mainApplicationContextProvider
                .postInitEvent()
                .startBootstrap();

        RpcTest bean = context.getBean(RpcTest.class);
        bean.r1();
    }

}
