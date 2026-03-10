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
public class DataRemove extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "dataRemove";
    }

    @Override public Object doAction(LuaToJavaDTO luaToJavaDTO) {
        String tableName = luaToJavaDTO.getString(0);
        Object uid = luaToJavaDTO.getObject(1);
        luaToJavaDTO.luaData().getVarDataTable().removeTableValue(tableName, uid);
        return null;
    }

}
