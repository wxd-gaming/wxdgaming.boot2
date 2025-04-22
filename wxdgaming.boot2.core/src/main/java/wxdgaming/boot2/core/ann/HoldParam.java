package wxdgaming.boot2.core.ann;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;

/**
 * 上下文持有参数
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 13:29
 */
@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.PARAMETER})
public @interface HoldParam {
}
