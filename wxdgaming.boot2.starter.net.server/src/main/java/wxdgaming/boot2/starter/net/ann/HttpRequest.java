package wxdgaming.boot2.starter.net.ann;

import java.lang.annotation.Documented;

/**
 * http 请求注解
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-10 08:57
 */
@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({
        java.lang.annotation.ElementType.METHOD,
})
public @interface HttpRequest {

    String path() default "";

    /** post or get */
    String method() default "";

    /** 权限 */
    int[] authority() default -1;

    /** 备注 */
    String comment() default "";

}
