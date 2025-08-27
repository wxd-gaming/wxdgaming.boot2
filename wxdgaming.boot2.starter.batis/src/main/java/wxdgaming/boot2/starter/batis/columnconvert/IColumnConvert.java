package wxdgaming.boot2.starter.batis.columnconvert;

import wxdgaming.boot2.starter.batis.TableMapping;

/**
 * 转化
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-27 11:44
 **/
public interface IColumnConvert {

    void register(AbstractColumnConvertFactory columnConvertFactory);

    /** 转换成数据库 */
    Object toDb(TableMapping.FieldMapping fieldMapping, Object fieldValue);

    Object fromDb(TableMapping.FieldMapping fieldMapping, Object dbValue);

}
