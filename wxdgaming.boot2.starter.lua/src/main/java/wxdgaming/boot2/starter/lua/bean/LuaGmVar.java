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
public class LuaGmVar {

    private ConcurrentHashMap<String, ConcurrentHashMap<Object, Object>> varMap = new ConcurrentHashMap<>();

    public void removeVar(String var) {
        ConcurrentHashMap<Object, Object> map = varMap.get(var);
        if (map == null) {
            return;
        }
        map.clear();
    }

    public void removeVar(String var, Object key) {
        ConcurrentHashMap<Object, Object> map = varMap.get(var);
        if (map == null) {
            return;
        }
        map.remove(key);
    }

    public void setVar(String var, Object key, Object value) {
        ConcurrentHashMap<Object, Object> map = varMap.computeIfAbsent(var, l -> new ConcurrentHashMap<>());
        map.put(key, value);
    }

    public void setVar(String var, Map<Object, Object> value) {
        ConcurrentHashMap<Object, Object> map = varMap.computeIfAbsent(var, l -> new ConcurrentHashMap<>());
        map.putAll(value);
    }

    public Map<Object, Object> getVar(String var) {
        return varMap.get(var);
    }

    public Object getVar(String var, Object key) {
        ConcurrentHashMap<Object, Object> map = varMap.get(var);
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

}
