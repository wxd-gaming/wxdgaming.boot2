package wxdgaming.game.test;

import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.RunApplicationMain;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.net.SocketScan;

/**
 * 启动器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 11:27
 **/
public class RobotApp {

    public static void main(String[] args) {

        RunApplicationMain runApplication = WxdApplication.run(
                CoreScan.class,
                SocketScan.class,
                RobotApp.class
        );

        runApplication.start();


    }

}
