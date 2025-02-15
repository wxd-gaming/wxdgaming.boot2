package wxdgaming.boot2.starter.batis.ann;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;

/**
 * 表 列 构建
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:47
 **/
@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({
        ElementType.FIELD,

})
public @interface DbColumn {

    boolean key() default false;

    boolean index() default false;

    String name() default "";

    /** 配置 */
    String columnDefinition() default "";

    /** 备注 */
    String comment() default "";

}
