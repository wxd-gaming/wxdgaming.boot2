package wxdgaming.boot2.starter.batis.columnconvert.impl;

import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
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
public class ColumnConvertBlob implements IColumnConvert {

    @Override public void register(AbstractColumnConvertFactory columnConvertFactory) {
        columnConvertFactory.register(ColumnType.Blob, this);
    }

    @Override public Object toDb(TableMapping.FieldMapping fieldMapping, Object fieldValue) {
        if (fieldValue == null) {
            if (fieldMapping.isNullable()) return null;
            return new byte[0];
        }
        if (!(fieldValue instanceof byte[])) {
            return FastJsonUtil.toJSONBytes(fieldValue);
        }
        return fieldValue;
    }

    @Override public Object fromDb(TableMapping.FieldMapping fieldMapping, Object dbValue) {
        if (dbValue == null) return null;
        return FastJsonUtil.parseSupportAutoType((byte[]) dbValue, fieldMapping.getJsonType());
    }

}
