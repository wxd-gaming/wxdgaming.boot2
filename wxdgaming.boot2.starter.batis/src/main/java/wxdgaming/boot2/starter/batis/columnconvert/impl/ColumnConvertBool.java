package wxdgaming.boot2.starter.batis.columnconvert.impl;

import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.columnconvert.AbstractColumnConvertFactory;
import wxdgaming.boot2.starter.batis.columnconvert.IColumnConvert;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Byte
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-27 13:27
 **/
public class ColumnConvertBool implements IColumnConvert {

    @Override public void register(AbstractColumnConvertFactory columnConvertFactory) {
        columnConvertFactory.register(ColumnType.Bool, this);
    }

    @Override public Object toDb(TableMapping.FieldMapping fieldMapping, Object fieldValue) {
        if (fieldValue == null) {
            if (fieldMapping.isNullable()) return null;
            return false;
        }
        if (fieldValue instanceof AtomicBoolean atomicBoolean) {
            fieldValue = atomicBoolean.get();
        }
        return fieldValue;
    }

    @Override public Object fromDb(TableMapping.FieldMapping fieldMapping, Object dbValue) {
        if (dbValue == null) return null;
        if (AtomicBoolean.class.isAssignableFrom(fieldMapping.getFileType())) {
            if (dbValue instanceof Boolean bool) {
                return new AtomicBoolean(bool);
            } else {
                return new AtomicBoolean(Boolean.parseBoolean(dbValue.toString()));
            }
        }
        if (dbValue instanceof Boolean bool) {
            return bool;
        } else {
            return Boolean.parseBoolean(dbValue.toString());
        }
    }
}
