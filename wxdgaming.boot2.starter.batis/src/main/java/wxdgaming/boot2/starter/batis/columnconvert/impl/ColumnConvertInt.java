package wxdgaming.boot2.starter.batis.columnconvert.impl;

import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.columnconvert.AbstractColumnConvertFactory;
import wxdgaming.boot2.starter.batis.columnconvert.IColumnConvert;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * int
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-27 13:27
 **/
public class ColumnConvertInt implements IColumnConvert {

    @Override public void register(AbstractColumnConvertFactory columnConvertFactory) {
        columnConvertFactory.register(ColumnType.Int, this);
    }

    @Override public Object toDb(TableMapping.FieldMapping fieldMapping, Object fieldValue) {
        if (fieldValue == null) {
            if (fieldMapping.isNullable()) return null;
            return 0;
        }
        if (fieldValue instanceof AtomicInteger atomicInteger) {
            return atomicInteger.get();
        }
        return fieldValue;
    }

    @Override public Object fromDb(TableMapping.FieldMapping fieldMapping, Object dbValue) {
        if (dbValue == null) return null;
        if (AtomicInteger.class.isAssignableFrom(fieldMapping.getFileType())) {
            if (dbValue instanceof Number number) {
                return new AtomicInteger(number.intValue());
            } else {
                return new AtomicInteger(Integer.parseInt(dbValue.toString()));
            }
        }
        if (dbValue instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(dbValue.toString());
    }
}
