package wxdgaming.boot2.core.runtime;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface IgnoreRunTimeRecord {
}
