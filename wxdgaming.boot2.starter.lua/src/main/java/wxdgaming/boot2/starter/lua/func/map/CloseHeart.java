package wxdgaming.boot2.starter.lua.func.map;

import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.lua.LuaInvokeJavaFunction;
import wxdgaming.boot2.starter.lua.LuaToJavaDTO;

/**
 * 开启地图心跳
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 13:54
 **/
@Component
public class CloseHeart extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "closeHeart";
    }

    @Override public Object doAction(LuaToJavaDTO luaToJavaDTO) {
        luaToJavaDTO.luaData().setOpenHeart(false);
        return null;
    }
}
