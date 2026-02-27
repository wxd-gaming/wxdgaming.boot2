package wxdgaming.boot2.starter.lua.func.data;

import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Lua;
import wxdgaming.boot2.starter.lua.LuaInvokeJavaFunction;
import wxdgaming.boot2.starter.lua.LuaUtils;
import wxdgaming.boot2.starter.lua.bean.LuaData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 13:22
 **/
@Slf4j
public class DataPutAll extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "dataPutAll";
    }

    @Override protected Object doAction(Lua L, Object[] args) {
        LuaData luaData = (LuaData) args[0];
        String row = args[1].toString();
        Map<String, Object> map = LuaUtils.object2MapStringObject(args[2]);
        luaData.getData().computeIfAbsent(row, k -> new ConcurrentHashMap<>()).putAll(map);
        return null;
    }

}
