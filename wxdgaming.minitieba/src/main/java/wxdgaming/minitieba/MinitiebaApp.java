package wxdgaming.minitieba;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import wxdgaming.boot2.core.ApplicationStartBuilder;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.batis.rocksdb.RocksDBScan;

/**
 * 迷你贴吧
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26 11:26
 **/
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreScan.class,
                RocksDBScan.class,
                MinitiebaApp.class,
        }
)
public class MinitiebaApp {

    public static void main(String[] args) {
        log.info("迷你贴吧服务启动中...");
        try {
            ApplicationStartBuilder.builder(MinitiebaApp.class)
                    .run(args)
                    .postInitEvent()
                    .startBootstrap();
            log.info("迷你贴吧服务启动完成...");
        } catch (Exception e) {
            log.error("迷你贴吧服务启动异常...", e);
            System.exit(99);
        }
    }

}
