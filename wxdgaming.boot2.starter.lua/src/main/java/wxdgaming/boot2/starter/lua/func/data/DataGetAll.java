package wxdgaming.boot2.starter.lua.func.data;

import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Lua;
import wxdgaming.boot2.starter.lua.LuaInvokeJavaFunction;
import wxdgaming.boot2.starter.lua.bean.LuaData;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 13:22
 **/
@Slf4j
public class DataGetAll extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "dataGetAll";
    }

    @Override protected Object doAction(Lua L, Object[] args) {
        LuaData luaData = (LuaData) args[0];
        ConcurrentHashMap<String, Object> stringObjectConcurrentHashMap = luaData.getData().get(args[1].toString());
        if (stringObjectConcurrentHashMap == null) {
            return null;
        }
        return new HashMap<>(stringObjectConcurrentHashMap);
    }

}
