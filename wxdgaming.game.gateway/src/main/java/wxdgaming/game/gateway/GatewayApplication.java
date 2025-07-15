package wxdgaming.game.gateway;

import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.RunApplicationMain;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.net.SocketScan;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;
import wxdgaming.game.login.LoginServiceGuiceModule;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-27 20:56
 **/
public class GatewayApplication {

    public static void main(String[] args) {
        RunApplicationMain runApplication = WxdApplication.run(
                CoreScan.class,
                SocketScan.class,
                ScheduledScan.class,
                LoginServiceGuiceModule.class,
                GatewayApplication.class
        );

        runApplication.start();
        runApplication.registerShutdownHook();
    }

}
