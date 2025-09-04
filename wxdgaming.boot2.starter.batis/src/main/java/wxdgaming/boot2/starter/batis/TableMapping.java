package wxdgaming.boot2.starter.batis;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.Throw;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.ParameterizedTypeImpl;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.ReflectClassProvider;
import wxdgaming.boot2.core.reflect.ReflectFieldProvider;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;
import wxdgaming.boot2.starter.batis.build.BuildColumnFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 数据表映射
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 16:14
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
        ReflectClassProvider reflectClassProvider = new ReflectClassProvider(cls);
        Map<String, Field> fieldMap = reflectClassProvider.getFieldMap();
        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            Field field = entry.getValue();
            ReflectFieldProvider fieldContext = reflectClassProvider.getFieldContext(field);
            DbColumn dbColumn = AnnUtil.ann(field, DbColumn.class);
            if (dbColumn != null && dbColumn.ignore()) {
                continue;
            }
            FieldMapping fieldMapping = new FieldMapping(fieldContext);
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
                BuildColumnFactory.getInstance().buildColumn(fieldMapping);
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

    @SuppressWarnings("unchecked")
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

        public FieldMapping(ReflectFieldProvider fieldContext) {
            this.field = fieldContext.getField();
            this.field.setAccessible(true);
            this.fileType = this.field.getType();
            this.jsonType = ParameterizedTypeImpl.genericFieldTypes(field);
            this.setMethod = fieldContext.getSetMethod();
            this.getMethod = fieldContext.getGetMethod();
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

        /**
         * @param bean     需要赋值的实例
         * @param colValue 数据库读取的数据
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        public void setValue(Object bean, Object colValue) {
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


        @Override public String toString() {
            return "FieldMapping{name='%s'}".formatted(columnName);
        }

    }

}
