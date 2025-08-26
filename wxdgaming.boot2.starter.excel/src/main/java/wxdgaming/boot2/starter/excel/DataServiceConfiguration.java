package wxdgaming.boot2.starter.excel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.starter.excel.store.DataRepository;

/**
 * guice 注册模块
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 14:00
 **/
@Configuration
public class DataServiceConfiguration {

    @Bean
    public DataRepository dataRepository() {
        return DataRepository.getIns();
    }

    @Bean
    public ExcelRepository excelRepository() {
        return ExcelRepository.getIns();
    }

}
