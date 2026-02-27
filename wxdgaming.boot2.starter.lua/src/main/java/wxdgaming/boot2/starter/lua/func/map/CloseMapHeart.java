package wxdgaming.boot2.starter.lua.func.map;

import party.iroiro.luajava.Lua;
import wxdgaming.boot2.starter.lua.LuaInvokeJavaFunction;
import wxdgaming.boot2.starter.lua.bean.LuaMap;

/**
 * 开启地图心跳
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 13:54
 **/
public class CloseMapHeart extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "closeHeart";
    }

    @Override protected Object doAction(Lua L, Object[] args) {
        LuaMap luaMap = (LuaMap) args[0];
        luaMap.setOpenHeart(false);
        return null;
    }
}
