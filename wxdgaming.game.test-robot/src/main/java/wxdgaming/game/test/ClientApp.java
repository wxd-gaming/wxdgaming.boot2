package wxdgaming.game.test;

import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.RunApplicationMain;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.net.NetScan;

/**
 * 启动器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 11:27
 **/
public class ClientApp {

    public static void main(String[] args) {

        RunApplicationMain runApplication = WxdApplication.run(
                CoreScan.class,
                NetScan.class,
                ClientApp.class
        );

        runApplication.start();


    }

}
