package wxdgaming.boot2.starter.lua;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.ImmutableLuaValue;

/**
 * long
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-08-29 11:38
 **/
public class LuaLong extends ImmutableLuaValue<Long> {

    public LuaLong(Lua L, Long value) {
        super(L, Lua.LuaType.NUMBER, value);
    }

    @Override
    public void push(Lua L) {
        L.push((long) value);
    }

    @Override public long toInteger() {
        return value;
    }

    @Override
    public Long toJavaObject() {
        return value;
    }

}
