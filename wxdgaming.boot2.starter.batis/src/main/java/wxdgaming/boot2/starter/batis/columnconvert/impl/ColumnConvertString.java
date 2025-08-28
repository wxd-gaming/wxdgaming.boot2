package wxdgaming.boot2.starter.batis.columnconvert.impl;

import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.columnconvert.AbstractColumnConvertFactory;
import wxdgaming.boot2.starter.batis.columnconvert.IColumnConvert;

import java.util.BitSet;

/**
 * int
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-27 13:27
 **/
public class ColumnConvertString implements IColumnConvert {

    @Override public void register(AbstractColumnConvertFactory columnConvertFactory) {
        columnConvertFactory.register(ColumnType.String, this);
    }

    @Override public Object toDb(TableMapping.FieldMapping fieldMapping, Object fieldValue) {
        if (fieldValue == null) {
            if (fieldMapping.isNullable()) return null;
            return "";
        }
        if (fieldValue instanceof Enum<?> enumObject) {
            return enumObject.name();
        } else if (fieldValue instanceof BitSet bitSet) {
            long[] longArray = bitSet.toLongArray();
            return FastJsonUtil.toJSONString(longArray);
        } else if (fieldValue instanceof ConfigString configString) {
            return configString.getValue();
        } else if (!(fieldValue instanceof String)) {
            return FastJsonUtil.toJSONString(fieldValue, FastJsonUtil.Writer_Features_Type_Name);
        }
        return fieldValue;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override public Object fromDb(TableMapping.FieldMapping fieldMapping, Object dbValue) {
        if (dbValue == null) return null;
        if (BitSet.class.isAssignableFrom(fieldMapping.getFileType())) {
            if (dbValue instanceof String json) {
                long[] parse = FastJsonUtil.parseSupportAutoType(json, long[].class);
                return BitSet.valueOf(parse);
            }
        } else if (Enum.class.isAssignableFrom(fieldMapping.getFileType())) {
            return Enum.valueOf((Class<Enum>) fieldMapping.getFileType(), dbValue.toString());
        } else if (ConfigString.class.isAssignableFrom(fieldMapping.getFileType())) {
            return new ConfigString(dbValue.toString());
        }
        return FastJsonUtil.parseSupportAutoType(dbValue.toString(), fieldMapping.getJsonType());
    }
}
