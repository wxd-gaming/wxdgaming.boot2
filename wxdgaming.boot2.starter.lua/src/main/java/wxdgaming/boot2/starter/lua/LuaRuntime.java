package wxdgaming.boot2.starter.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Lua;
import wxdgaming.boot2.core.reflect.ReflectProvider;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * lua 装载器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-08-22 16:10
 */
@Slf4j
@Getter
public class LuaRuntime implements Closeable {

    final String name;
    final Supplier<Lua> luaFactory;
    final LuaFileRequire luaFileRequire;

    final ConcurrentHashMap<String, Object> globals = new ConcurrentHashMap<>();
    volatile ConcurrentHashMap<Thread, LuacContext> contexts = new ConcurrentHashMap<>();

    public LuaRuntime(String name, String dir, Supplier<Lua> luaFactory) {
        this.name = name;
        this.luaFactory = luaFactory;
        LuaFileCache luaFileCache = new LuaFileCache(dir);
        luaFileRequire = new LuaFileRequire(luaFileCache);
        log.info("luaPath:{}", luaFileRequire.getLuaPath());

        // 初始化扫描器（指定扫描包和扫描类型）
        ReflectProvider reflectProvider = ReflectProvider
                .Builder
                .of(Thread.currentThread().getContextClassLoader(), LuaInvokeJavaFunction.class.getPackageName())
                .setFilter(LuaInvokeJavaFunction.class::isAssignableFrom)
                .build();
        reflectProvider.classWithSuper(LuaInvokeJavaFunction.class).forEach(aClass -> {
            try {
                LuaInvokeJavaFunction luaInvokeJavaFunction = aClass.getDeclaredConstructor().newInstance();
                Object put = globals.put(luaInvokeJavaFunction.cmd(), luaInvokeJavaFunction);
                if (put != null) {
                    log.error("重复注册 {}", luaInvokeJavaFunction.cmd());
                }
            } catch (Exception e) {
                log.error("初始化失败", e);
            }
        });
    }

    public LuacContext context() {
        LuacContext luaContext = contexts.get(Thread.currentThread());
        if (luaContext == null || luaContext.isClosed()) {
            luaContext = new LuacContext(this, luaFactory);
            contexts.put(Thread.currentThread(), luaContext);
        }
        return luaContext;
    }

    public void release() {
        LuacContext luaContext = contexts.remove(Thread.currentThread());
        if (luaContext != null) {
            luaContext.close();
        }
    }

    /** 单位KB */
    public long memory() {
        AtomicLong memory = new AtomicLong();
        memory(memory);
        return memory.get();
    }

    public void memory(AtomicLong memory) {
        contexts.values().forEach(luacContext -> luacContext.memory(memory));
    }

    public Object onEvent(String key, Object... args) {
        return context().onEvent(key, args);
    }

    public Object postEvent(String key, Object... args) {
        return context().postEvent(key, args);
    }

    public long size() {
        return contexts.size();
    }

    /** 关闭资源 */
    @Override public void close() {
        if (contexts == null) return;
        contexts.values().forEach(LuacContext::close);
        contexts = null;
    }


}
