package wxdgaming.boot2.starter.lua.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 变量测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-01-07 16:44
 **/
@Getter
@Setter
public class VarTable {

    private ConcurrentHashMap<String, ConcurrentHashMap<Object, Object>> varTable = new ConcurrentHashMap<>();

    public void removeTable(String tableName) {
        varTable.remove(tableName);
    }

    public void removeTableValue(String tableName, Object key) {
        ConcurrentHashMap<Object, Object> map = varTable.get(tableName);
        if (map == null) {
            return;
        }
        map.remove(key);
        if (map.isEmpty()) {
            varTable.remove(tableName);
        }
    }

    public void setTable(String tableName, Map<Object, Object> value) {
        computeIfAbsent(tableName).putAll(value);
    }

    public void setTableValue(String tableName, Object key, Object value) {
        computeIfAbsent(tableName).put(key, value);
    }

    public Map<Object, Object> computeIfAbsent(String tableName) {
        return varTable.computeIfAbsent(tableName, l -> new ConcurrentHashMap<>());
    }

    public Map<Object, Object> getTable(String tableName) {
        return varTable.get(tableName);
    }

    public Object getTableValue(String tableName, Object key) {
        ConcurrentHashMap<Object, Object> map = varTable.get(tableName);
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

}
