package code.proxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.executor.ThreadStopWatch;
import wxdgaming.boot2.core.proxy.MainThreadStopWatchAspect;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-17 19:44
 **/
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootTest(classes = {CoreScan.class, MainThreadStopWatchAspect.class, UserServiceImpl.class})
public class ProxyTest {

    @Autowired
    UserServiceImpl userService;

    @Test
    public void testProxy() {
        System.out.println("dddddd");
        ThreadStopWatch.init("d");
        userService.addUser("张三");
        String string = ThreadStopWatch.releasePrint();
        System.out.println(string);
    }

}
