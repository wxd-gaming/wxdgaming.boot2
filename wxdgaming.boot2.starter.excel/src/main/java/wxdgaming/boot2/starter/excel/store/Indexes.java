package wxdgaming.boot2.starter.excel.store;

import java.lang.annotation.*;

/**
 * 仓库 key
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-03-15 21:25
 */
@Inherited
@Documented
@Repeatable(Indexes.List.class)
@Target({ElementType.TYPE/*类*/})
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexes {

    /** key值 */
    String[] value() default {};

    /**
     * 容器注解，用于支持重复注解
     */
    @Inherited
    @Documented
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Indexes[] value();
    }


}
