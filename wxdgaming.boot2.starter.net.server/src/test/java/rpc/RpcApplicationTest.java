package rpc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import wxdgaming.boot2.core.ApplicationStartBuilder;
import wxdgaming.boot2.core.CoreScan;
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

        ApplicationStartBuilder.builder(RpcApplicationTest.class)
                .run(args)
                .postInitEvent()
                .startBootstrap();

        RpcTest bean = SpringUtil.mainApplicationContextProvider.getBean(RpcTest.class);
        bean.r1();

    }

}
