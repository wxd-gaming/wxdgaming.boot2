package wxdgaming.boot2.starter.lua.func.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.lua.LuaInvokeJavaFunction;
import wxdgaming.boot2.starter.lua.LuaToJavaDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 13:22
 **/
@Slf4j
@Component
public class DataPutAll extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "dataPutAll";
    }

    @Override public Object doAction(LuaToJavaDTO luaToJavaDTO) {
        String row = luaToJavaDTO.getString(0);
        Map<String, Object> map = luaToJavaDTO.getMapStringObject(1);
        luaToJavaDTO.luaData().getData().computeIfAbsent(row, k -> new ConcurrentHashMap<>()).putAll(map);
        return null;
    }

}
