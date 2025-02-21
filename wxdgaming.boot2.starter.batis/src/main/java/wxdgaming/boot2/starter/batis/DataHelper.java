package wxdgaming.boot2.starter.batis;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.util.List;

/**
 * 数据集
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:36
 **/
@Getter
@Setter
public abstract class DataHelper<DDL extends DDLBuilder> {

    protected DDL ddlBuilder;

    public DataHelper(DDL ddlBuilder) {
        this.ddlBuilder = ddlBuilder;
    }

    public TableMapping tableMapping(Class<? extends Entity> cls) {
        return ddlBuilder.tableMapping(cls);
    }

    public abstract Connection connection();

    public abstract <R extends Entity> int tableCount(Class<R> cls);

    public abstract int tableCount(String tableName);

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
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-02-16 01:15
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
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-02-16 01:15
     */
    public abstract <R extends Entity> R findByKey(String tableName, Class<R> cls, Object... args);

    /**
     * 根据主键查询数据是否已经在数据库
     *
     * @param entity 实体对象
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-02-16 01:16
     */
    public abstract boolean existBean(Entity entity);

    /** 保存数据  如果数据主键不在数据库 insert 存在数据库 update */
    public void save(Entity entity) {
        if (existBean(entity)) {
            entity.setNewEntity(false);
            update(entity);
        } else {
            insert(entity);
        }
    }

    public abstract void insert(Entity entity);

    public abstract void update(Entity entity);

}
