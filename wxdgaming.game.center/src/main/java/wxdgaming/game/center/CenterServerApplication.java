package wxdgaming.game.center;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import wxdgaming.boot2.core.ApplicationStartBuilder;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.batis.rocksdb.RocksDBScan;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientScan;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;
import wxdgaming.boot2.starter.tailf.TailfScan;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;

/**
 * 中心服务启动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-15 09:55
 **/
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreScan.class,
                HttpClientScan.class,
                ScheduledScan.class,
                PgsqlScan.class,
                RocksDBScan.class,
                TailfScan.class,
                ConnectLoginProperties.class,
                CenterServerApplication.class
        }
)
public class CenterServerApplication {

    public static void main(String[] args) {
        ApplicationStartBuilder.builder(CenterServerApplication.class)
                .run(args)
                .postInitEvent()
                .startBootstrap();
    }

}
