package wxdgaming.game.login;

import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.RunApplicationMain;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.batis.sql.pgsql.MysqlScan;
import wxdgaming.boot2.starter.net.SocketScan;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientScan;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-27 20:56
 **/
public class LoginApplication {

    public static void main(String[] args) {
        RunApplicationMain runApplication = WxdApplication.run(
                CoreScan.class,
                HttpClientScan.class,
                SocketScan.class,
                ScheduledScan.class,
                MysqlScan.class,
                LoginServiceGuiceModule.class,
                LoginApplication.class
        );

        runApplication.start();
        runApplication.registerShutdownHook();
    }

}
