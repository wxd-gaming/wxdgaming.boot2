package wxdgaming.boot2.starter.lua;

import com.alibaba.fastjson2.JSONArray;
import party.iroiro.luajava.Lua;
import wxdgaming.boot2.starter.lua.bean.LuaData;

import java.util.List;
import java.util.Map;

/**
 * lua调用java的时候数据传递
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-27 20:46
 **/
public record LuaToJavaDTO(Lua L, LuaData luaData, JSONArray args) {

    public int getIntValue(int index) {
        return args.getIntValue(index);
    }

    public int getIntValue(int index, int defaultValue) {
        if (args.size() <= index) {
            return defaultValue;
        }
        return args.getIntValue(index);
    }

    public String getString(int index) {
        return args.getString(index);
    }

    public Object getObject(int index) {
        return args.get(index);
    }

    public Map<Object, Object> getMapObjectObject(int index) {
        return LuaUtils.object2MapObjectObject(args.get(index));
    }

    public Map<String, Object> getMapStringObject(int index) {
        return LuaUtils.object2MapStringObject(args.get(index));
    }

    public List<Map<String, String>> getListMapStringString(int index) {
        return LuaUtils.object2ListMapStringString(args.get(index));
    }

}
