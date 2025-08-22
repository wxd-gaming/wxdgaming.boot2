package wxdgaming.boot2.starter.batis.sql.pgsql;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.CoreScan;

/**
 * 扫描器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-22 09:56
 **/
@ComponentScan(basePackageClasses = {
        CoreScan.class,
        PgsqlProperties.class,
        PgsqlConfiguration.class
})
@Component
public class PgsqlScan {
}
