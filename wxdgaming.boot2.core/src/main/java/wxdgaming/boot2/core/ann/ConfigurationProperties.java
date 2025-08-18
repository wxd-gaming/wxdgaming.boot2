package wxdgaming.boot2.core.ann;

import java.lang.annotation.*;

/**
 * 配置属性注解
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 09:07
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ConfigurationProperties {

    /** 配置前缀 */
    String prefix();

}
