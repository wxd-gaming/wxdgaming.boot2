package wxdgaming.boot2.starter.lua.func.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.lua.LuaInvokeJavaFunction;
import wxdgaming.boot2.starter.lua.LuaToJavaDTO;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 13:22
 **/
@Slf4j
@Component
public class DataGetAll extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "dataGetAll";
    }

    @Override public Object doAction(LuaToJavaDTO luaToJavaDTO) {
        String tableName = luaToJavaDTO.getString(0);
        Map<Object, Object> map = luaToJavaDTO.luaData().getVarDataTable().getTable(tableName);
        if (map == null) {
            return Collections.emptyMap();
        }
        return new HashMap<>(map);
    }

}
