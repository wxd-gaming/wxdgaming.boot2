package wxdgaming.boot2.starter.lua.impl;

import lombok.Getter;
import party.iroiro.luajava.AbstractLua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.cleaner.LuaReference;
import party.iroiro.luajava.lua55.Lua55;
import party.iroiro.luajava.value.LuaValue;
import wxdgaming.boot2.starter.lua.LuaLong;

/**
 * lua54 重写 Lua54 pushArray to pushJavaArray
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-01 10:38
 */
@Getter
public class Lua55Impl extends Lua55 {

    private final String name;
    private boolean closed = false;

    public Lua55Impl() throws LinkageError {
        this.name = Thread.currentThread().getName();
    }

    public Lua55Impl(long L, int id, AbstractLua main) {
        super(L, id, main);
        this.name = Thread.currentThread().getName();
    }

    @Override public Lua55Impl newThread() {
        return (Lua55Impl) super.newThread();
    }

    @Override protected Lua55Impl newThread(long L, int id, AbstractLua mainThread) {
        return new Lua55Impl(L, id, mainThread);
    }

    @Override public LuaValue get() {
        return super.get();
    }

    @Override public LuaValue from(long n) {
        return new LuaLong(this, n);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override public void checkStack(int extra) throws RuntimeException {
        for (LuaReference ref = (LuaReference) this.recyclableReferences.poll(); ref != null; ref = (LuaReference) this.recyclableReferences.poll()) {
            this.recordedReferences.remove(ref.getReference());
            this.unref(ref.getReference());
        }
    }

    @Override public void pushArray(Object array) throws IllegalArgumentException {
        pushJavaArray(array);
    }

    @Override public void pCall(int nArgs, int nResults) throws LuaException {
        super.pCall(nArgs, nResults);
    }

    @Override protected void checkError(int code, boolean runtime) throws LuaException {
        super.checkError(code, runtime);
    }

    @Override public void close() {
        if (closed) return;
        try {
            super.close();
        } catch (Throwable ignore) {}
        closed = true;
    }

    @Override public String toString() {
        return "Lua54{name='%s', closed=%s}".formatted(name, closed);
    }
}
