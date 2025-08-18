package wxdgaming.boot2.starter.batis.ann;

import java.lang.annotation.Documented;

/**
 * 表 列
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 12:47
 **/
@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({
        java.lang.annotation.ElementType.TYPE
})
public @interface DbTable {

    String tableName() default "";

    String tableComment() default "";

}
