package wxdgaming.boot2.starter.batis.ann;

import wxdgaming.boot2.starter.batis.ColumnType;

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

    /** 忽略字段 */
    boolean ignore() default false;

    boolean key() default false;

    boolean index() default false;

    /** 字段名字 */
    String columnName() default "";

    ColumnType columnType() default ColumnType.None;

    int length() default 0;

    boolean nullable() default true;

    /** 备注 */
    String comment() default "";

}
