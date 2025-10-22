package wxdgaming.boot2.core.runtime;

import java.lang.annotation.*;

/**
 * 耗时统计忽略函数
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-22 19:17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface IgnoreRunTimeRecord {
}
