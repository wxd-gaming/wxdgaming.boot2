package wxdgaming.boot2.starter.batis.sql.pgsql;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * pgsql 模块
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 12:42
 **/
@Configuration
@EnableConfigurationProperties(PgsqlProperties.class)
public class PgsqlConfiguration {

    final PgsqlProperties pgsqlProperties;

    public PgsqlConfiguration(PgsqlProperties pgsqlProperties) {
        this.pgsqlProperties = pgsqlProperties;
    }

    @Bean
    @Primary
    public PgsqlDataHelper pgsqlDataHelper() {
        return new PgsqlDataHelper(pgsqlProperties.getPgsql());
    }

    @Bean("db.pgsql-second")
    @ConditionalOnProperty(name = "db.pgsql-second.url")
    public PgsqlDataHelper pgsqlDataHelper2() {
        return new PgsqlDataHelper(pgsqlProperties.getPgsqlSecond());
    }

    @Bean("db.pgsql-third")
    @ConditionalOnProperty(name = "db.pgsql-third.url")
    public PgsqlDataHelper pgsqlDataHelper3() {
        return new PgsqlDataHelper(pgsqlProperties.getPgsqlThird());
    }

}
