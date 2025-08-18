package wxdgaming.boot2.starter.excel.store;

import java.lang.annotation.*;

/**
 * 数据映射注解
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-10-09 10:55
 */
@Inherited
@Documented
@Target({ElementType.TYPE/*类*/})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataMapping {

    /** 名字 */
    String name();

    /** 备注, 注解，注释 */
    String comment();

    /** 引用 excel 文件路径 */
    String excelPath();

    /** excel sheet name */
    String sheetName();
}
