package wxdgaming.boot2.core.ann;

import java.lang.annotation.Documented;

/**
 * 在调用stop之前调用
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-10 14:41
 */
@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD})
public @interface StopBefore {
}
