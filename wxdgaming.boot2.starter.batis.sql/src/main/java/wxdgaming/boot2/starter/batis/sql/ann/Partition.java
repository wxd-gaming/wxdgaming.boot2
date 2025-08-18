package wxdgaming.boot2.starter.batis.sql.ann;

import java.lang.annotation.*;

/**
 * 分区信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 22:57
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Partition {

    /** 由于 mysql 初始化表 添加分区信息必须要有 最小分区 */
    String mysqlInitMinRangeValue() default "0";

    /** 初始化表的时候添加分区信息 */
    String[] initRangeArrays() default "";

}
