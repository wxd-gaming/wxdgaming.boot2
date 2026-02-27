package wxdgaming.boot2.starter.lua.func.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.lua.LuaInvokeJavaFunction;
import wxdgaming.boot2.starter.lua.LuaToJavaDTO;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 13:22
 **/
@Slf4j
@Component
public class DataPut extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "dataPut";
    }

    @Override public Object doAction(LuaToJavaDTO luaToJavaDTO) {
        String row = luaToJavaDTO.getString(0);
        String cell = luaToJavaDTO.getString(1);
        Object data = luaToJavaDTO.getObject(2);
        return luaToJavaDTO.luaData().getData().computeIfAbsent(row, k -> new ConcurrentHashMap<>()).put(cell, data);
    }

}
