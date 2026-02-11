package wxdgaming.boot2.starter.date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.CoreScan;

/**
 * 时间服务扫描
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:14
 **/
@Slf4j
@ComponentScan(basePackageClasses = {CoreScan.class, DateService.class})
@Component
public class DateScan {

}
