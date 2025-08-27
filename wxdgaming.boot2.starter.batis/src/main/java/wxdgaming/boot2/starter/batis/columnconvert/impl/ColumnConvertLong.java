package wxdgaming.boot2.starter.batis.columnconvert.impl;

import wxdgaming.boot2.core.lang.TimeValue;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.columnconvert.AbstractColumnConvertFactory;
import wxdgaming.boot2.starter.batis.columnconvert.IColumnConvert;

import java.util.concurrent.atomic.AtomicLong;

/**
 * int
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-27 13:27
 **/
public class ColumnConvertLong implements IColumnConvert {

    @Override public void register(AbstractColumnConvertFactory columnConvertFactory) {
        columnConvertFactory.register(ColumnType.Long, this);
    }

    @Override public Object toDb(TableMapping.FieldMapping fieldMapping, Object fieldValue) {
        if (fieldValue == null) {
            if (fieldMapping.isNullable()) return null;
            return 0L;
        }
        if (fieldValue instanceof AtomicLong atomicLong) {
            return atomicLong.get();
        } else if (fieldValue instanceof TimeValue timeValue) {
            return timeValue.longValue();
        }
        return fieldValue;
    }

    @Override public Object fromDb(TableMapping.FieldMapping fieldMapping, Object dbValue) {
        if (dbValue == null) return null;
        if (AtomicLong.class.isAssignableFrom(fieldMapping.getFileType())) {
            if (dbValue instanceof Number number) {
                return new AtomicLong(number.longValue());
            } else {
                return new AtomicLong(Long.parseLong(dbValue.toString()));
            }
        }
        if (TimeValue.class.isAssignableFrom(fieldMapping.getFileType())) {
            if (dbValue instanceof Number number) {
                return new TimeValue(number.longValue());
            } else {
                return new TimeValue(Long.parseLong(dbValue.toString()));
            }
        }
        if (dbValue instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(dbValue.toString());
    }
}
