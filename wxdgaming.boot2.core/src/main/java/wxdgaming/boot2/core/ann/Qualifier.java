package wxdgaming.boot2.core.ann;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;

@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface Qualifier {

    boolean required() default true;

}
