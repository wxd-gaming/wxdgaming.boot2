package wxdgaming.boot2.starter.lua;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.ImmutableLuaValue;

/**
 * long
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-08-29 11:38
 **/
public class LuaNil extends ImmutableLuaValue<Long> {

    public static final LuaNil NIL = new LuaNil();

    public LuaNil() {
        super(null, Lua.LuaType.NIL, null);
    }

    @Override
    public void push(Lua L) {
        throw new UnsupportedOperationException();
    }

    @Override public long toInteger() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long toJavaObject() {
        throw new UnsupportedOperationException();
    }

}
