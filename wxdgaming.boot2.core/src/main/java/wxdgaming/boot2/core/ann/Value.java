package wxdgaming.boot2.core.ann;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;

@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({
        java.lang.annotation.ElementType.METHOD,
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.LOCAL_VARIABLE
})
public @interface Value {

    /** 属性名字 */
    String path();

    /**
     * 嵌套的参数路由，比如数据是在json内，可以使用a.b.c嵌套获取参数
     * <p>参数: {a:{b:{c:"a"}}}
     */
    boolean nestedPath() default false;

    /** 必须 */
    boolean required() default true;

    /** 默认值 */
    String defaultValue() default "";
}
