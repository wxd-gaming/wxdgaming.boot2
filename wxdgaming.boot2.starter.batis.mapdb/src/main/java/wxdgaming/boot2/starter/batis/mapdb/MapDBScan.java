package wxdgaming.boot2.starter.batis.mapdb;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * 扫描器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-01 09:22
 **/
@ComponentScan(basePackageClasses = {MapdbProperties.class, MapDBDataHelper.class})
@Component
public class MapDBScan {
}
