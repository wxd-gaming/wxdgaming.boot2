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
public class DataGet extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "dataGet";
    }

    @Override public Object doAction(LuaToJavaDTO luaToJavaDTO) {
        ConcurrentHashMap<Object, Object> map = luaToJavaDTO.luaData().getVarDataTable().get(luaToJavaDTO.getString(0));
        if (map == null) {
            return null;
        }
        return map.get(luaToJavaDTO.getObject(1));
    }

}
