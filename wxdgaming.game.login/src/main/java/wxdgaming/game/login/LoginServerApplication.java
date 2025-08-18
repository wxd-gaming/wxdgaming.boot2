package wxdgaming.game.login;

import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.RunApplicationMain;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan;
import wxdgaming.boot2.starter.net.SocketScan;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientScan;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;
import wxdgaming.game.basic.login.LoginProperties;
import wxdgaming.game.basic.slog.SlogService;
import wxdgaming.logbus.LogBusService;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-27 20:56
 **/
public class LoginServerApplication {

    public static void main(String[] args) {
        RunApplicationMain runApplication = WxdApplication.run(
                CoreScan.class,
                HttpClientScan.class,
                SocketScan.class,
                ScheduledScan.class,
                PgsqlScan.class,
                SlogService.class,
                LogBusService.class,
                LoginServerApplication.class
        );

        runApplication.start();
        runApplication.registerShutdownHook();
    }

}
