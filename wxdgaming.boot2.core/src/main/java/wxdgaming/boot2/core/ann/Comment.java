package wxdgaming.boot2.core.ann;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 注释
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 14:05
 */
@Documented
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({
        ElementType.TYPE,
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.LOCAL_VARIABLE
})
public @interface Comment {
    String value();
}
