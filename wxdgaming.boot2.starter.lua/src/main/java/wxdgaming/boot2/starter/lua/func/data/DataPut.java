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
public class DataPut extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "dataPut";
    }

    @Override public Object doAction(LuaToJavaDTO luaToJavaDTO) {
        String tableName = luaToJavaDTO.getString(0);
        Object uid = luaToJavaDTO.getObject(1);
        Object data = luaToJavaDTO.getObject(2);
        luaToJavaDTO.luaData().getVarDataTable().setTableValue(tableName, uid, data);
        return null;
    }

}
