package wxdgaming.game.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.util.JvmUtil;
import wxdgaming.boot2.starter.batis.mapdb.MapDBScan;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan;
import wxdgaming.boot2.starter.excel.DataExcelScan;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientScan;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;
import wxdgaming.game.basic.slog.SlogService;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.logbus.LogBusService;

/**
 * 登录
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-27 20:56
 **/
@Slf4j
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreScan.class,
                DataExcelScan.class,
                HttpClientScan.class,
                ScheduledScan.class,
                PgsqlScan.class,
                MapDBScan.class,
                SlogService.class,
                GlobalDataService.class,
                LogBusService.class,
                LoginServerApplication.class
        }
)
public class LoginServerApplication {

    public static void main(String[] args) {
        try {
            new SpringApplicationBuilder(LoginServerApplication.class).run(args);
            SpringUtil.mainApplicationContextProvider
                    .executeMethodWithAnnotatedInit()
                    .startBootstrap();
        } catch (Exception e) {
            log.error("登录服务启动异常...", e);
            JvmUtil.halt(99);
        }
    }

}
