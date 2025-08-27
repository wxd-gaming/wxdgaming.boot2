package wxdgaming.boot2.starter.batis.columnconvert.impl;

import wxdgaming.boot2.core.util.NumberUtil;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.columnconvert.AbstractColumnConvertFactory;
import wxdgaming.boot2.starter.batis.columnconvert.IColumnConvert;

/**
 * Byte
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-27 13:27
 **/
public class ColumnConvertByte implements IColumnConvert {

    @Override public void register(AbstractColumnConvertFactory columnConvertFactory) {
        columnConvertFactory.register(ColumnType.Byte, this);
    }

    @Override public Object toDb(TableMapping.FieldMapping fieldMapping, Object fieldValue) {
        if (fieldValue == null) {
            if (fieldMapping.isNullable()) return null;
            return (byte) 0;
        }
        return fieldValue;
    }

    @Override public Object fromDb(TableMapping.FieldMapping fieldMapping, Object dbValue) {
        if (dbValue == null) return null;
        return NumberUtil.parseByte(dbValue, 0);
    }
}
