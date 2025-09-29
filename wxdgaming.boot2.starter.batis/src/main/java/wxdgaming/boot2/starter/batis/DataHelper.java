package wxdgaming.boot2.starter.batis;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.ann.Stop;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 数据集
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 12:36
 **/
@Getter
@Setter
public abstract class DataHelper {

    protected DDLBuilder ddlBuilder;

    public DataHelper(DDLBuilder ddlBuilder) {
        this.ddlBuilder = ddlBuilder;
    }

    @SuppressWarnings("unchecked")
    public <DDL extends DDLBuilder> DDL ddlBuilder() {
        return (DDL) ddlBuilder;
    }

    @Stop
    public abstract void stop();

    public TableMapping tableMapping(Class<? extends Entity> cls) {
        return ddlBuilder.tableMapping(cls);
    }

    public abstract Connection connection();

    public abstract <R extends Entity> long tableCount(Class<R> cls);

    public abstract long tableCount(String tableName);

    public abstract List<JSONObject> queryListByEntity(Class<? extends Entity> cls);

    public abstract List<JSONObject> queryListByTableName(String tableName);

    /** 查询表的所有数据 */
    public abstract <R extends Entity> List<R> findList(Class<R> cls);

    /** 查询表的所有数据 */
    public abstract <R extends Entity> List<R> findList(String tableName, Class<R> cls);

    /**
     * 根据主键值查询
     *
     * @param cls  返回的数据实体类
     * @param args 参数
     * @param <R>  实体模型
     * @return
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2025-02-16 01:15
     */
    public abstract <R extends Entity> R findByKey(Class<R> cls, Object... args);

    /**
     * 根据主键值查询
     *
     * @param tableName 表明
     * @param cls       返回的数据实体类
     * @param args      参数
     * @param <R>       实体模型
     * @return
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2025-02-16 01:15
     */
    public abstract <R extends Entity> R findByKey(String tableName, Class<R> cls, Object... args);

    /**
     * 根据主键查询数据是否已经在数据库
     *
     * @param entity 实体对象
     * @return
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2025-02-16 01:16
     */
    public abstract boolean existBean(Entity entity);

    /** 保存数据  如果数据主键不在数据库 insert 存在数据库 update */
    public void save(Entity entity) {
        if (Boolean.FALSE.equals(entity.getNewEntity()) || existBean(entity)) {
            entity.setNewEntity(false);
            update(entity);
        } else {
            insert(entity);
        }
    }

    public abstract void insert(Entity entity);

    public abstract void update(Entity entity);

    public abstract void delete(Entity entity);

    public void saveList(List<? extends Entity> entityList, BiConsumer<String, Entity> errorCallback) {
        List<Entity> insertList = new ArrayList<>();
        List<Entity> updateList = new ArrayList<>();
        for (Entity entity : entityList) {
            if (Boolean.FALSE.equals(entity.getNewEntity()) || existBean(entity)) {
                entity.setNewEntity(false);
                updateList.add(entity);
            } else {
                insertList.add(entity);
            }
        }
        insertList(insertList, errorCallback == null ? null : entity -> errorCallback.accept("insert", entity));
        updateList(updateList, errorCallback == null ? null : entity -> errorCallback.accept("update", entity));
    }

    public abstract void insertList(List<? extends Entity> entityList, Consumer<Entity> errorCallback);

    public abstract void updateList(List<? extends Entity> entityList, Consumer<Entity> errorCallback);

    public abstract <R extends Entity> void deleteByKey(Class<R> cls, Object... args);

    public abstract <R extends Entity> void deleteByKey(String tableName, Class<R> cls, Object... args);

    public abstract <R extends Entity> void deleteByWhere(Class<R> cls, String where, Object... args);

    public abstract void deleteByWhere(String tableName, String where, Object... args);

    /** 清库 */
    public abstract void truncates();

    /** 清空表 */
    public <R extends Entity> void truncate(Class<R> cls) {
        TableMapping tableMapping = tableMapping(cls);
        truncate(tableMapping.getTableName());
    }

    /** 清空表 */
    public abstract void truncate(String tableName);

}
