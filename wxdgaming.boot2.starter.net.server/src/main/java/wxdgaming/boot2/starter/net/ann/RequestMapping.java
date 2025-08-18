package wxdgaming.boot2.starter.net.ann;

import java.lang.annotation.Documented;

/**
 * 路由注解
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-14 17:24
 */
@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({
        java.lang.annotation.ElementType.TYPE,
})
public @interface RequestMapping {

    String value() default "";

}
