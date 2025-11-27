package wxdgaming.boot2.core.proxy;

import java.lang.annotation.*;

/**
 * 切面排除
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-27 08:57
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.TYPE,
        ElementType.METHOD,
        ElementType.LOCAL_VARIABLE
})
public @interface IgnoreAspect {
}
