package wxdgaming.boot2.starter.net.server.ann;

import java.lang.annotation.Documented;

@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({
        java.lang.annotation.ElementType.METHOD,
})
public @interface HttpRequest {

    String path() default "";

    String method() default "";

    /** 权限 */
    boolean authority() default false;
}
