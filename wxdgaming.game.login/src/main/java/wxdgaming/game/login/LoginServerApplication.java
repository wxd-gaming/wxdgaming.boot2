package wxdgaming.game.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import wxdgaming.boot2.core.ApplicationStartBuilder;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.util.JvmUtil;
import wxdgaming.boot2.starter.batis.mapdb.MapDBScan;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientScan;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;
import wxdgaming.boot2.starter.tailf.TailfScan;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.common.slog.SlogService;
import wxdgaming.logbus.LogBusService;

/**
 * 登录
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-27 20:56
 **/
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreScan.class,
                HttpClientScan.class,
                ScheduledScan.class,
                PgsqlScan.class,
                MapDBScan.class,
                SlogService.class,
                GlobalDataService.class,
                LogBusService.class,
                TailfScan.class,
                LoginServerApplication.class
        }
)
public class LoginServerApplication {

    public static void main(String[] args) {
        try {
            ApplicationStartBuilder.builder(LoginServerApplication.class)
                    .run(args)
                    .postInitEvent()
                    .startBootstrap();
        } catch (Exception e) {
            log.error("登录服务启动异常...", e);
            JvmUtil.halt(99);
        }
    }

}
