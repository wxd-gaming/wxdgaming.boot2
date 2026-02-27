package wxdgaming.boot2.starter.lua;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.JFunction;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

/**
 * JFun
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-08-26 11:01
 */
@Slf4j
public abstract class LuaInvokeJavaFunction implements JFunction {

    @Override public int __call(Lua L) {
        Object[] _args = null;
        try {
            int oldTop = L.getTop();
            _args = new Object[oldTop];
            for (int i = 0; i < _args.length; i++) {
                LuaValue luaValue1 = L.get();
                Object javaObject = LuaUtils.luaValue2Object(luaValue1);
                _args[_args.length - i - 1] = javaObject;
            }
            L.setTop(oldTop);
            Object results = doAction(L, _args);
            if (results != null) {
                LuaUtils.push(L, results);
            }
            return results == null ? 0 : 1;
        } catch (Throwable e) {
            String jsonString = "";
            try {
                jsonString = JSON.toJSONString(_args);
            } catch (Exception ignore) {}
            log.error("call lua function error " + jsonString, e);
            throw new RuntimeException("call lua function error " + jsonString, e);
        }
    }

    public abstract String cmd();

    protected abstract Object doAction(Lua L, Object[] args);

}
