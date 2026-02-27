package wxdgaming.boot2.starter.lua.func.data;

import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Lua;
import wxdgaming.boot2.starter.lua.LuaInvokeJavaFunction;
import wxdgaming.boot2.starter.lua.bean.LuaData;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 13:22
 **/
@Slf4j
public class DataPut extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "dataPut";
    }

    @Override protected Object doAction(Lua L, Object[] args) {

        LuaData luaData = (LuaData) args[0];
        String row = args[1].toString();
        String cell = args[2].toString();
        Object data = args[3];
        return luaData.getData().computeIfAbsent(row, k -> new ConcurrentHashMap<>()).put(cell, data);
    }

}
