package wxdgaming.boot2.core.ann;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;

@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.LOCAL_VARIABLE
})
public @interface ThreadParam {

    /** 属性名字 path */
    String path() default "";

    /** 必须 */
    boolean required() default true;

    /** 默认值 */
    String defaultValue() default "";
}
