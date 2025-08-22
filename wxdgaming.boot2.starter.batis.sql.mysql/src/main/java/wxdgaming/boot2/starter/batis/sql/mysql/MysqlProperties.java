package wxdgaming.boot2.starter.batis.sql.mysql;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;

/**
 * 配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-20 16:55
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "db")
public class MysqlProperties {

    SqlConfig mysql;
    SqlConfig mysqlSecond;
    SqlConfig mysqlThird;

}
