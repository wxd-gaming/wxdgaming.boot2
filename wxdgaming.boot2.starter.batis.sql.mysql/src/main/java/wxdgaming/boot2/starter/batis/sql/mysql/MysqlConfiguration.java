package wxdgaming.boot2.starter.batis.sql.mysql;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * mysql 模块
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 12:42
 **/
@Configuration
@EnableConfigurationProperties(MysqlProperties.class)
public class MysqlConfiguration {

    final MysqlProperties mysqlProperties;

    public MysqlConfiguration(MysqlProperties mysqlProperties) {
        this.mysqlProperties = mysqlProperties;
    }

    @Bean
    @Primary
    public MysqlDataHelper mysqlDataHelper() {
        return new MysqlDataHelper(mysqlProperties.getMysql());
    }

    @Bean("db.mysql-second")
    @ConditionalOnProperty(name = "db.mysql-second.url")
    public MysqlDataHelper mysqlDataHelper2() {
        return new MysqlDataHelper(mysqlProperties.getMysqlSecond());
    }

    @Bean("db.mysql-third")
    @ConditionalOnProperty(name = "db.mysql-third.url")
    public MysqlDataHelper mysqlDataHelper3() {
        return new MysqlDataHelper(mysqlProperties.getMysqlThird());
    }

}
