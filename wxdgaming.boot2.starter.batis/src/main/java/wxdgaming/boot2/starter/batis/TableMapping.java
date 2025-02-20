package wxdgaming.boot2.starter.batis;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.chatset.json.ParameterizedTypeImpl;
import wxdgaming.boot2.core.reflect.FieldUtils;
import wxdgaming.boot2.core.reflect.MethodUtil;
import wxdgaming.boot2.core.util.AnnUtil;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

        Map<String, Field> fields = FieldUtils.getFields(cls, false);
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
        if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Bool;
        } else if (byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Byte;
        } else if (short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Short;
        } else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Int;
        } else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Long;
        } else if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Float;
        } else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
            fieldMapping.columnType = ColumnType.Double;
        } else if (byte[].class.isAssignableFrom(type)) {
            if (fieldMapping.length == 0)
                fieldMapping.length = 65535;
            fieldMapping.columnType = ColumnType.Blob;
        } else if (String.class.isAssignableFrom(type)) {
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

        public Object getValue(Object bean) {
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

        public Object toDbValue(Object bean) {
            try {
                Object object;
                if (getMethod == null) {
                    object = field.get(bean);
                } else {
                    object = getMethod.invoke(bean);
                }
                if (object != null) {
                    switch (columnType) {
                        case String -> {
                            if (!(object instanceof String)) {
                                object = FastJsonUtil.toJson(object, FastJsonUtil.Writer_Features_Type_Name_NOT_ROOT);
                            }
                        }
                        case Json -> {
                            if (!(object instanceof String)) {
                                object = FastJsonUtil.toJson(object, FastJsonUtil.Writer_Features);
                            }
                        }
                        case Blob -> {
                            if (!(object instanceof byte[])) {
                                object = FastJsonUtil.toBytes(object);
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

        public void setValue(Object bean, Object colValue) {
            try {
                if (setMethod == null) {
                    if (Modifier.isFinal(field.getModifiers())) {
                        if (Map.class.isAssignableFrom(field.getType())) {
                            final Map fieldValue = (Map) getValue(bean);
                            fieldValue.putAll((Map) colValue);
                        } else if (List.class.isAssignableFrom(field.getType())) {
                            final List fieldValue = (List) getValue(bean);
                            fieldValue.addAll((List) colValue);
                        } else if (Set.class.isAssignableFrom(field.getType())) {
                            final Set fieldValue = (Set) getValue(bean);
                            fieldValue.addAll((Set) colValue);
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
                    } else {
                        field.set(bean, colValue);
                    }
                } else {
                    setMethod.invoke(bean, colValue);
                }
            } catch (Exception e) {
                throw Throw.of(e);
            }
        }

        @Override public String toString() {
            return "FieldMapping{name='%s'}".formatted(columnName);
        }

    }

}
