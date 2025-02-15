package wxdgaming.boot2.starter.batis;

import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据集
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:36
 **/
@Getter
@Setter
public abstract class DataHelper {

    protected final Map<Class<?>, TableMapping> tableMappings = new ConcurrentHashMap<>();

    public abstract Connection connection();

    public TableMapping tableMapping(Class<?> cls) {
        return tableMappings.computeIfAbsent(cls, l -> new TableMapping(cls));
    }

    public abstract <R> List<R> findAll(Class<R> cls);

    public abstract <R> List<R> findAll(String tableName, Class<R> cls);

    public abstract <R> R findById(Class<R> cls, Object... args);

    public abstract <R> R findById(String tableName, Class<R> cls, Object... args);

    public abstract void insert(Object object);

    public abstract void update(Object object);

}
