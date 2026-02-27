package wxdgaming.boot2.starter.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import party.iroiro.luajava.Lua;
import wxdgaming.boot2.core.HoldApplicationContext;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * lua 服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-10-21 16:11
 */
@Slf4j
@Getter
@Service
public class LuaService extends HoldApplicationContext {


    final AtomicReference<LuaRuntime> luaRuntime = new AtomicReference<>();

    public LuaService() {
    }

    public void init(String paths, Supplier<Lua> luaFactory) {
        Map<String, LuaInvokeJavaFunction> functionMap = getApplicationContextProvider().toMap(LuaInvokeJavaFunction.class, i -> i.cmd());
        LuaRuntime _luaRuntime = new LuaRuntime("root", paths, luaFactory, functionMap);
        LuaRuntime old = luaRuntime.get();
        luaRuntime.set(_luaRuntime);

        if (old != null) {
            new Thread(() -> {
                try {
                    Thread.sleep(30_000);
                    old.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

    }

    public Object onEvent(String key, Object... args) {
        return getRuntime().onEvent(key, args);
    }

    public Object postEvent(String key, Object... args) {
        return getRuntime().postEvent(key, args);
    }

    public LuacContext luaContext() {
        return getRuntime().context();
    }

    public void release() {
        getRuntime().release();
    }

    public LuaRuntime getRuntime() {
        return luaRuntime.get();
    }

    public long memory() {
        AtomicLong atomicLong = new AtomicLong();
        getRuntime().memory(atomicLong);
        return atomicLong.get();
    }

    public long size() {
        return getRuntime().size();
    }

    public void close() {
        getRuntime().close();
    }

}
