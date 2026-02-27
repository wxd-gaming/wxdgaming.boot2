package wxdgaming.boot2.starter.lua.func.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.lua.LuaInvokeJavaFunction;
import wxdgaming.boot2.starter.lua.LuaToJavaDTO;

/**
 * 数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 13:22
 **/
@Slf4j
@Component
public class DataClear extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "dataClear";
    }

    @Override public Object doAction(LuaToJavaDTO luaToJavaDTO) {
        String row = luaToJavaDTO.getString(0);
        luaToJavaDTO.luaData().getData().remove(row);
        return null;
    }

}
