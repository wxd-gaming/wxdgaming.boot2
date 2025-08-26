package wxdgaming.boot2.starter.excel;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.CoreScan;

/**
 * 扫描器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-08-08 09:25
 **/
@ComponentScan(basePackageClasses = {
        CoreScan.class,
        DataServiceConfiguration.class
})
@Component
public class DataExcelScan {
}
