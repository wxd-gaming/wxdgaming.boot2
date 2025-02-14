package wxdgaming.boot2.core.ann;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;

@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({
        ElementType.TYPE,
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.LOCAL_VARIABLE
})
public @interface Sort {

    int value() default 99999;

}
