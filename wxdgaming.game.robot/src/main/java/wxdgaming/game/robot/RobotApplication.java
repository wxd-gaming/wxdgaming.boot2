package wxdgaming.game.robot;

import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.RunApplicationMain;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.excel.DataExcelScan;
import wxdgaming.boot2.starter.net.SocketScan;
import wxdgaming.boot2.starter.scheduled.ScheduledProperties;
import wxdgaming.game.basic.login.LoginProperties;

/**
 * 启动器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-27 11:27
 **/
public class RobotApplication {

    public static void main(String[] args) {

        RunApplicationMain runApplication = WxdApplication.run(
                CoreScan.class,
                LoginProperties.class,
                SocketScan.class,
                DataExcelScan.class,
                ScheduledProperties.class,
                RobotApplication.class
        );

        runApplication.start();
        runApplication.registerShutdownHook();

    }

}
