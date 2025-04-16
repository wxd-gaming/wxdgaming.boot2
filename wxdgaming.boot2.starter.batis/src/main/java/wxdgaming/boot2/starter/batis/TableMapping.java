package wxdgaming.boot2.starter.batis;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.util.TypeUtils;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.chatset.json.ParameterizedTypeImpl;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.boot2.core.lang.TimeValue;
import wxdgaming.boot2.core.reflect.FieldUtil;
import wxdgaming.boot2.core.reflect.MethodUtil;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.core.util.AnnUtil;
import wxdgaming.boot2.starter.batis.ann.Convert;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;
import wxdgaming.boot2.starter.batis.convert.ConvertFactory;
import wxdgaming.boot2.starter.batis.convert.Converter;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 数据表映射
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 16:14
 **/
@Getter
@Setter
public class TableMapping {

    public static String tableName(Class<?> cls) {
        String tmpTableName = cls.getSimpleName();
        DbTable table = AnnUtil.ann(cls, DbTable.class);
        if (table != null) {
            if (StringUtils.isNotBlank(table.tableName())) {
                tmpTableName = table.tableName();
            }
        }
        /*表名全小写*/
        return tmpTableName.toLowerCase();
    }

    public static String beanTableName(Object bean) {
        if (bean instanceof EntityName entityName) return entityName.tableName().toLowerCase();
        return tableName(bean.getClass());
    }

    public static String tableComment(Class<?> cls) {
        String tmpTableComment = cls.getSimpleName();
        DbTable table = AnnUtil.ann(cls, DbTable.class);
        if (table != null) {
            if (StringUtils.isNotBlank(table.tableComment())) {
                tmpTableComment = table.tableComment();
            }
        }
        /*表名全小写*/
        return tmpTableComment;
    }


    private final Class<?> cls;
    private final String tableName;
    private final String tableComment;
    /** 主键字段 */
    private final List<FieldMapping> keyFields = new ArrayList<>();
    /** 所有的列 */
    private final LinkedHashMap<String, FieldMapping> columns = new LinkedHashMap<>();

    /** 完全查询 */
    private final Map<String, String> selectSql = new ConcurrentHashMap<>();
    /** 根据主键查询 key: tableName, value: sql语句 */
    private final Map<String, String> selectByKeySql = new ConcurrentHashMap<>();
    /** 根据主键查询 key: tableName, value: sql语句 */
    private final Map<String, String> exitSql = new ConcurrentHashMap<>();
    /** 插入 key: tableName, value: sql语句 */
    private final Map<String, String> insertSql = new ConcurrentHashMap<>();
    /** 主键列更新  key: tableName, value: sql语句 */
    private final Map<String, String> updateSql = new ConcurrentHashMap<>();

    public TableMapping(Class<?> cls) {
        this.cls = cls;
        /*表名全小写*/
        this.tableName = tableName(cls);
        this.tableComment = tableComment(cls);

        Map<String, Field> fields = FieldUtil.getFields(false, cls);
        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            Field field = entry.getValue();
            DbColumn dbColumn = AnnUtil.ann(field, DbColumn.class);
            if (dbColumn != null && dbColumn.ignore()) {
                continue;
            }
            FieldMapping fieldMapping = new FieldMapping(cls, field);
            if (dbColumn != null) {
                fieldMapping.key = dbColumn.key();
                fieldMapping.index = dbColumn.index();
                fieldMapping.columnName = dbColumn.columnName();
                fieldMapping.columnType = dbColumn.columnType();
                fieldMapping.nullable = dbColumn.nullable();
                fieldMapping.length = dbColumn.length();
                fieldMapping.comment = dbColumn.comment();
            }
            if (fieldMapping.columnType == null || fieldMapping.columnType == ColumnType.None) {
                buildColumnType(fieldMapping);
            }

            if (StringUtils.isBlank(fieldMapping.comment)) {
                fieldMapping.comment = fieldMapping.field.getName();
            }

            if (fieldMapping.isKey()) {
                keyFields.add(fieldMapping);
            }
            if (StringUtils.isBlank(fieldMapping.columnName)) {
                fieldMapping.columnName = field.getName();
            }
            /*数据库列名全小写*/
            fieldMapping.columnName = fieldMapping.columnName.toLowerCase();
            columns.put(fieldMapping.columnName, fieldMapping);
        }

        if (keyFields.isEmpty()) {
            throw new RuntimeException(cls + " 类不存在主键 ");
        }

    }


    public void buildColumnType(FieldMapping fieldMapping) {
        Class<?> type = fieldMapping.getField().getType();
        if (AtomicReference.class.isAssignableFrom(type)) {
            type = ReflectContext.getTType(fieldMapping.getField().getGenericType(), 0);
        }
        if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Bool;
        } else if (byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Byte;
        } else if (short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Short;
        } else if (int.class.isAssignableFrom(type)
                   || Integer.class.isAssignableFrom(type)
                   || AtomicInteger.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Int;
        } else if (long.class.isAssignableFrom(type)
                   || Long.class.isAssignableFrom(type)
                   || AtomicLong.class.isAssignableFrom(type)
                   || TimeValue.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Long;
        } else if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Float;
        } else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Double;
        } else if (byte[].class.isAssignableFrom(type)) {
            if (fieldMapping.length == 0)
                fieldMapping.length = 65535;
            fieldMapping.columnType = ColumnType.Blob;
        } else if (String.class.isAssignableFrom(type)
                   || Enum.class.isAssignableFrom(type)
                   || ConfigString.class.isAssignableFrom(type)) {
            if (fieldMapping.length == 0)
                fieldMapping.length = 256;
            fieldMapping.columnType = ColumnType.String;
        } else {
            fieldMapping.columnType = ColumnType.Json;
        }
    }

    public <R> R newInstance() {
        try {
            Constructor<?> constructor = cls.getConstructor();
            return (R) constructor.newInstance();
        } catch (Exception e) {
            throw Throw.of(e);
        }
    }

    @Getter
    @Setter
    public class FieldMapping {

        private final Field field;
        private Class<?> fileType;
        private Type jsonType;
        private final Method setMethod;
        private final Method getMethod;
        private String columnName;
        private ColumnType columnType;
        private int length;
        private boolean nullable = true;
        private boolean key;
        private boolean index;
        private String comment;
        private String defaultValue;

        public FieldMapping(Class<?> cls, Field field) {
            this.field = field;
            this.field.setAccessible(true);
            this.fileType = this.field.getType();
            this.jsonType = ParameterizedTypeImpl.genericFieldTypes(field);
            this.setMethod = MethodUtil.findSetMethod(cls, field);
            this.getMethod = MethodUtil.findGetMethod(cls, field);
        }

        /** 获取字段的值 */
        public Object getFieldValue(Object bean) {
            try {
                Object object;
                if (getMethod == null) {
                    object = field.get(bean);
                } else {
                    object = getMethod.invoke(bean);
                }
                return object;
            } catch (Exception e) {
                throw Throw.of(e);
            }
        }

        /** 获取字段的值，转换成数据库可用 */
        public Object toDbValue(Object bean) {
            try {
                Object object = getFieldValue(bean);

                Convert ann = AnnUtil.ann(getField(), Convert.class);
                if (ann != null) {
                    Class<? extends Converter> cls = ann.value();
                    Converter<Object, Object> converter = ConvertFactory.getConverter(cls);
                    if (converter != null) {
                        return converter.toDb(object);
                    }
                }

                if (object != null) {
                    if (object instanceof AtomicReference<?> atomicReference) {
                        object = atomicReference.get();
                    }
                }
                if (object != null) {
                    switch (columnType) {
                        case Bool -> {
                            if (object instanceof AtomicBoolean atomicBoolean) {
                                object = atomicBoolean.get();
                            }
                        }
                        case Int -> {
                            if (object instanceof AtomicInteger atomicInteger) {
                                object = atomicInteger.get();
                            }
                        }
                        case Long -> {
                            if (object instanceof AtomicLong atomicLong) {
                                object = atomicLong.get();
                            } else if (object instanceof TimeValue timeValue) {
                                object = timeValue.longValue();
                            }
                        }
                        case String -> {
                            if (object instanceof Enum<?> enumObject) {
                                object = enumObject.name();
                            } else if (object instanceof ConfigString configString) {
                                object = configString.getValue();
                            } else if (!(object instanceof String)) {
                                object = FastJsonUtil.toJSONString(object, FastJsonUtil.Writer_Features_Type_Name_NOT_ROOT);
                            }
                        }
                        case Json -> {
                            if (object instanceof BitSet bitSet) {
                                long[] longArray = bitSet.toLongArray();
                                object = FastJsonUtil.toJSONString(longArray);
                            } else if (!(object instanceof String)) {
                                object = FastJsonUtil.toJSONString(object, FastJsonUtil.Writer_Features);
                            }
                        }
                        case Blob -> {
                            if (!(object instanceof byte[])) {
                                object = FastJsonUtil.toJSONBytes(object);
                            }
                        }
                        case null, default -> {

                        }
                    }
                }
                return object;
            } catch (Exception e) {
                throw Throw.of(e);
            }
        }

        /**
         * @param bean 需要赋值的实例
         * @param data 数据库读取的行数据
         * @author: wxd-gaming(無心道, 15388152619)
         * @version: 2025-02-24 09:39
         */
        public void setValue(Object bean, JSONObject data) {
            Object colValue = data.get(getColumnName());
            colValue = fromDbValue(colValue);
            if (colValue == null) return;
            try {
                if (setMethod == null) {
                    if (Modifier.isFinal(field.getModifiers())) {
                        if (Map.class.isAssignableFrom(getFileType())) {
                            final Map<?, ?> fieldValue = (Map<?, ?>) getFieldValue(bean);
                            fieldValue.putAll((Map) colValue);
                        } else if (List.class.isAssignableFrom(getFileType())) {
                            final List<?> fieldValue = (List<?>) getFieldValue(bean);
                            fieldValue.addAll((List) colValue);
                        } else if (Set.class.isAssignableFrom(getFileType())) {
                            final Set<?> fieldValue = (Set<?>) getFieldValue(bean);
                            fieldValue.addAll((Set) colValue);
                        } else if (AtomicReference.class.isAssignableFrom(getFileType())) {
                            if (!(colValue instanceof AtomicReference)) {
                                final AtomicReference<Object> fieldValue = (AtomicReference) getFieldValue(bean);
                                fieldValue.set(colValue);
                            }
                        } else {
                            throw new RuntimeException(
                                    "映射表：%s \n字段：%s \n类型：%s \n数据库配置值：%s; 最终类型异常"
                                            .formatted(
                                                    TableMapping.this.tableName,
                                                    field.getName(),
                                                    field.getType(),
                                                    colValue.getClass()
                                            )
                            );
                        }
                    } else if (AtomicReference.class.isAssignableFrom(getFileType())) {
                        if (!(colValue instanceof AtomicReference)) {
                            final AtomicReference<Object> fieldValue = (AtomicReference) getFieldValue(bean);
                            fieldValue.set(colValue);
                        }
                    } else {
                        field.set(bean, colValue);
                    }
                } else {
                    setMethod.invoke(bean, colValue);
                }
            } catch (Exception e) {
                throw Throw.of(this.getField().toString(), e);
            }
        }

        /** 从数据库加载转化成实体bean数据 */
        protected Object fromDbValue(Object object) {
            if (object == null) {
                return null;
            }

            if (getFileType().isAssignableFrom(object.getClass())) {
                return object;
            }

            Convert ann = AnnUtil.ann(getField(), Convert.class);
            if (ann != null) {
                Class<? extends Converter> cls = ann.value();
                Converter<Object, Object> converter = ConvertFactory.getConverter(cls);
                if (converter != null) {
                    Object parsed = TypeUtils.cast(object.toString(), converter.getClazzY());
                    return converter.fromDb(getJsonType(), parsed);
                }
            }

            if (AtomicReference.class.isAssignableFrom(getFileType())) {
                Class<?> tType = ReflectContext.getTType(getField().getGenericType(), 0);
                if (String.class.isAssignableFrom(tType)) {
                    return new AtomicReference<>(object);
                }
                object = FastJsonUtil.parse(object.toString(), tType);
            }

            switch (getColumnType()) {
                case Blob -> {
                    return FastJsonUtil.parse((byte[]) object, getJsonType());
                }
                case Bool -> {
                    if (AtomicBoolean.class.isAssignableFrom(getFileType())) {
                        return new AtomicBoolean(Boolean.parseBoolean(object.toString()));
                    }
                    if (object instanceof Boolean b) {
                        return b;
                    }
                    return Boolean.parseBoolean(object.toString());
                }
                case Byte -> {
                    if (object instanceof Number b) {
                        return b.byteValue();
                    }
                    return Byte.parseByte(object.toString());
                }
                case Short -> {
                    if (object instanceof Number b) {
                        return b.shortValue();
                    }
                    return Short.parseShort(object.toString());
                }
                case Int -> {
                    if (AtomicInteger.class.isAssignableFrom(getFileType())) {
                        if (object instanceof Number number) {
                            return new AtomicInteger(number.intValue());
                        } else {
                            return new AtomicInteger(Integer.parseInt(object.toString()));
                        }
                    }
                    if (object instanceof Number number) {
                        return number.intValue();
                    }
                    return Integer.parseInt(object.toString());
                }
                case Long -> {
                    if (AtomicLong.class.isAssignableFrom(getFileType())) {
                        if (object instanceof Number number) {
                            return new AtomicLong(number.longValue());
                        } else {
                            return new AtomicLong(Long.parseLong(object.toString()));
                        }
                    }
                    if (TimeValue.class.isAssignableFrom(getFileType())) {
                        if (object instanceof Number number) {
                            return new TimeValue(number.longValue());
                        } else {
                            return new TimeValue(Long.parseLong(object.toString()));
                        }
                    }
                    if (object instanceof Number number) {
                        return number.longValue();
                    }
                    return Long.parseLong(object.toString());
                }
                case Double -> {
                    if (object instanceof Number number) {
                        return number.doubleValue();
                    }
                    return Double.parseDouble(object.toString());
                }
                case Float -> {
                    if (object instanceof Number number) {
                        return number.floatValue();
                    }
                    return Float.parseFloat(object.toString());
                }
                case null, default -> {
                    if (BitSet.class.isAssignableFrom(getFileType())) {
                        if (object instanceof String json) {
                            long[] parse = FastJsonUtil.parse(json, long[].class);
                            return BitSet.valueOf(parse);
                        }
                    } else if (Enum.class.isAssignableFrom(getFileType())) {
                        return Enum.valueOf((Class<Enum>) getFileType(), object.toString());
                    } else if (ConfigString.class.isAssignableFrom(getFileType())) {
                        return new ConfigString(object.toString());
                    }
                    return FastJsonUtil.parse(object.toString(), getJsonType());
                }
            }
        }

        @Override public String toString() {
            return "FieldMapping{name='%s'}".formatted(columnName);
        }

    }

}
