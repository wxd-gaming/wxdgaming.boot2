package wxdgaming.boot2.starter.batis.columnconvert;

import com.alibaba.fastjson2.util.TypeUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.ann.Convert;
import wxdgaming.boot2.starter.batis.convert.AbstractConverter;
import wxdgaming.boot2.starter.batis.convert.ConvertFactory;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractColumnConvertFactory {

    HashMap<ColumnType, IColumnConvert> columnConvertTable = new HashMap<>();

    public void register(ColumnType columnType, IColumnConvert columnConvert) {
        IColumnConvert put = columnConvertTable.put(columnType, columnConvert);
        AssertUtil.assertTrue(put == null, "columnConvertTable.put()");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object toDbValue(TableMapping.FieldMapping fieldMapping, Object bean) {

        Object dbValue = fieldMapping.getFieldValue(bean);

        Convert ann = AnnUtil.ann(fieldMapping.getField(), Convert.class);
        if (ann != null) {
            Class<? extends AbstractConverter> cls = ann.value();
            AbstractConverter<Object, Object> abstractConverter = ConvertFactory.getConverter(cls);
            if (abstractConverter != null) {
                return abstractConverter.toDb(dbValue);
            }
        }

        if (dbValue != null) {
            if (dbValue instanceof AtomicReference<?> atomicReference) {
                dbValue = atomicReference.get();
            }
        }

        IColumnConvert columnConvert = columnConvertTable.get(fieldMapping.getColumnType());
        if (columnConvert == null) {
            return dbValue;
        }
        return columnConvert.toDb(fieldMapping, dbValue);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object fromDbValue(TableMapping.FieldMapping fieldMapping, Object dbValue) {

        if (dbValue == null) {
            return null;
        }

        if (fieldMapping.getFileType().isAssignableFrom(dbValue.getClass())) {
            return dbValue;
        }

        Convert ann = AnnUtil.ann(fieldMapping.getField(), Convert.class);
        if (ann != null) {
            Class<? extends AbstractConverter> cls = ann.value();
            AbstractConverter<Object, Object> abstractConverter = ConvertFactory.getConverter(cls);
            if (abstractConverter != null) {
                Object parsed = TypeUtils.cast(dbValue.toString(), abstractConverter.getClazzY());
                return abstractConverter.fromDb(fieldMapping.getJsonType(), parsed);
            }
        }

        if (AtomicReference.class.isAssignableFrom(fieldMapping.getFileType())) {
            Class<?> tType = ReflectProvider.getTType(fieldMapping.getField().getGenericType(), 0);
            if (String.class.isAssignableFrom(tType)) {
                return new AtomicReference<>(dbValue);
            }
            dbValue = FastJsonUtil.parseSupportAutoType(dbValue.toString(), tType);
        }

        IColumnConvert columnConvert = columnConvertTable.get(fieldMapping.getColumnType());
        if (columnConvert == null) {
            return dbValue;
        }
        return columnConvert.fromDb(fieldMapping, dbValue);
    }

}
