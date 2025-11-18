package wxdgaming.boot2.starter.batis.sql.pgsql;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wxdgaming.boot2.core.MainApplicationContextProvider;
import wxdgaming.boot2.starter.batis.sql.SqlDataBatch;

/**
 * pgsql 模块
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 12:42
 **/
@Configuration
@EnableConfigurationProperties(PgsqlProperties.class)
public class PgsqlConfiguration {

    final MainApplicationContextProvider mainApplicationContextProvider;
    final PgsqlProperties pgsqlProperties;

    public PgsqlConfiguration(MainApplicationContextProvider mainApplicationContextProvider, PgsqlProperties pgsqlProperties) {
        this.mainApplicationContextProvider = mainApplicationContextProvider;
        this.pgsqlProperties = pgsqlProperties;
    }

    @Bean
    @Primary
    public PgsqlDataHelper pgsqlDataHelper() {
        PgsqlDataHelper pgsqlDataHelper = new PgsqlDataHelper(pgsqlProperties.getPgsql());
        SqlDataBatch dataBatch = pgsqlDataHelper.getDataBatch();
        SqlDataBatch sqlDataBatch = mainApplicationContextProvider.registerInstance(dataBatch);
        pgsqlDataHelper.setDataBatch(sqlDataBatch);
        return pgsqlDataHelper;
    }

    @Bean
    @Primary
    public PgsqlDataCacheService pgsqlDataCacheService(PgsqlDataHelper pgsqlDataHelper) {
        return new PgsqlDataCacheService(pgsqlDataHelper);
    }

    @Bean("db.pgsql-second")
    @ConditionalOnProperty(name = "db.pgsql-second.url")
    public PgsqlDataHelper pgsqlDataHelper2() {
        return new PgsqlDataHelper(pgsqlProperties.getPgsqlSecond());
    }

    @Bean("db.pgsql-cache-second")
    @ConditionalOnBean(name = {"db.pgsql-second"})
    public PgsqlDataCacheService pgsqlDataCacheService2(@Qualifier("db.pgsql-second") PgsqlDataHelper pgsqlDataHelper) {
        return new PgsqlDataCacheService(pgsqlDataHelper);
    }

    @Bean("db.pgsql-third")
    @ConditionalOnProperty(name = "db.pgsql-third.url")
    public PgsqlDataHelper pgsqlDataHelper3() {
        return new PgsqlDataHelper(pgsqlProperties.getPgsqlThird());
    }

    @Bean("db.pgsql-cache-third")
    @ConditionalOnBean(name = {"db.pgsql-third"})
    public PgsqlDataCacheService pgsqlDataCacheService3(@Qualifier("db.pgsql-third") PgsqlDataHelper pgsqlDataHelper) {
        return new PgsqlDataCacheService(pgsqlDataHelper);
    }
}
