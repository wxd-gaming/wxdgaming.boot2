package wxdgaming.boot2.starter.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.event.Level;
import party.iroiro.luajava.Consts;
import party.iroiro.luajava.JuaAPI;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

import java.nio.Buffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * lua 当前上下文
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-10-21 16:57
 */
@Slf4j
@Getter
public class LuacContext {

    protected final ReentrantLock lock = new ReentrantLock();
    protected boolean closed = false;
    protected final String name;
    protected final Lua L;
    HashMap<String, LuaValue> funcCache = new HashMap<>();
    HashMap<String, Boolean> checkFuncCache = new HashMap<>();

    public LuacContext(LuaRuntime luacRuntime, Supplier<Lua> luaFactory) {
        L = luaFactory.get();
        this.name = luacRuntime.getName() + " - " + Thread.currentThread().getName();
        L.openLibraries();
        LuaFileRequire luaFileRequire = luacRuntime.getLuaFileRequire();
        {
            // 设置 Lua 文件的搜索路径
            L.getGlobal("package");
            L.getField(-1, "path");
            String currentPath = L.toString(-1);
            String newPath = luaFileRequire.getLuaPath() + ";" + currentPath;
            L.pop(1); // 移除当前路径
            L.push(newPath);
            L.setField(-2, "path");
            L.set("paths", luaFileRequire.getLuaPath());
        }

        for (Map.Entry<String, Object> entry : luacRuntime.getGlobals().entrySet()) {
            L.set(entry.getKey(), entry.getValue());
        }

        List<String> modules = luaFileRequire.getModules();
        requireLoad(modules, 1);

        Level level;

        if (log.isTraceEnabled()) level = Level.TRACE;
        else if (log.isDebugEnabled()) level = Level.DEBUG;
        else if (log.isInfoEnabled()) level = Level.INFO;
        else if (log.isWarnEnabled()) level = Level.WARN;
        else level = Level.ERROR;

        int levelInt = level.toInt() / 10;
        onEvent("setloglevel", levelInt);

    }

    /**
     * 通过文件字节加载
     *
     * @param list    需要配加载的文件列表
     * @param fortune 加载权重，1为不重试，2为重试一次，3为重试两次，以此类推，默认为1
     */
    public boolean load(List<ImmutablePair<Path, byte[]>> list, int fortune) {
        if (fortune < 1) return false;
        List<ImmutablePair<Path, byte[]>> error = new ArrayList<>();
        for (ImmutablePair<Path, byte[]> immutablePair : list) {
            String string = immutablePair.getLeft().getFileName().toString();
            try {
                loadFile4Bytes(string, immutablePair.getRight(), fortune);
            } catch (Exception e) {
                if (fortune > 1) {
                    error.add(immutablePair);
                } else {
                    log.error(string, e);
                }
            }
        }
        if (!error.isEmpty()) {
            return load(error, fortune - 1);
        }
        return true;
    }

    /**
     * require 加载文件形式会缓存，只加载一次，dofile 调用一次加载一次，不会缓存，
     *
     * @param modules 需要加载模块
     * @param fortune 加载权重，1为不重试，2为重试一次，3为重试两次，以此类推，默认为1
     */
    void requireLoad(List<String> modules, int fortune) {
        if (fortune < 1) return;
        List<String> error = new ArrayList<>();
        for (String module : modules) {
            String luaScript = "require('" + module + "')";
            try {
                L.run(luaScript);
                log.debug("require load lua {}", module);
            } catch (Exception e) {
                if (fortune > 1) {
                    error.add(module);
                } else {
                    throw new RuntimeException(luaScript, e);
                }
            }
        }
        if (!error.isEmpty()) {
            requireLoad(error, fortune - 1);
        }
    }

    public void loadFile4Bytes(String fileName, byte[] bytes, int fortune) {
        Buffer flip = JuaAPI.allocateDirect(bytes.length).put(bytes).flip();
        L.run(flip, fileName);
        log.debug("file byte load lua {}", fileName);
    }

    public LuaValue findLuaValue(String name) {
        return funcCache.computeIfAbsent(name, f -> L.get(name));
    }

    private boolean cache(String checkName, String eventName) {
        return checkFuncCache.computeIfAbsent(eventName, f -> {
            Object checkHasEvent = dispatch(checkName, eventName);
            if (Boolean.TRUE.equals(checkHasEvent)) {
                return true;
            }
            log.error("dispatch {}, {} - {} not function", this.toString(), checkName, eventName);
            return false;
        });
    }

    /** 触发一级函数 */
    public Object onEvent(String eventName, Object... args) {
        boolean cache = cache("checkHasEvent", eventName);
        if (!cache) {
            return null;
        }
        return dispatch("onEvent", eventName, args);
    }

    /** 触发二级函数 */
    public Object postEvent(String eventName, Object... args) {
        boolean cache = cache("checkHasPostEvent", eventName);
        if (!cache) {
            return null;
        }
        return dispatch("postEvent", eventName, args);
    }

    private Object dispatch(String dispatch, String eventName, Object... args) {
        lock.lock();
        try {
            LuaValue dispatchLua = findLuaValue(dispatch);
            if (dispatchLua == null
                || dispatchLua.type() != Lua.LuaType.FUNCTION) {
                log.error("dispatch {} - {} not found", this.toString(), dispatch);
                return null;
            }
            Object[] args2 = new Object[args.length + 1];
            System.arraycopy(args, 0, args2, 1, args.length);
            args2[0] = eventName;
            return pcall0(dispatchLua, args2);
        } finally {
            lock.unlock();
        }
    }

    Object pcall0(LuaValue luaValue, Object... args) {
        int oldTop = L.getTop();
        luaValue.push(L);
        for (Object o : args) {
            LuaUtils.push(L, o);
        }
        try {
            L.pCall(args.length, Consts.LUA_MULTRET);
            int returnCount = L.getTop() - oldTop;
            if (returnCount == 0) {
                return null;
            }
            LuaValue[] call = new LuaValue[returnCount];
            for (int i = 0; i < returnCount; i++) {
                call[returnCount - i - 1] = L.get();
            }
            LuaValue returnValue = call[0];
            return LuaUtils.luaValue2Object(returnValue);
        } catch (Throwable e) {
            RuntimeException runtimeException = new RuntimeException(e.getMessage());
            runtimeException.setStackTrace(e.getStackTrace());
            throw runtimeException;
        } finally {
            L.setTop(oldTop);
        }
    }

    public void memory(AtomicLong memory) {
        lock.lock();
        try {
            LuaValue luaValue = findLuaValue("memory0");
            Object pcall = pcall0(luaValue);
            memory.addAndGet(((Number) pcall).longValue());
        } finally {
            lock.unlock();
        }
    }

    public void gc() {
        lock.lock();
        try {
            if (closed) return;
            try {
                this.L.gc();
            } catch (Exception e) {
                log.error("{} - cleanup error {}", this.toString(), e.toString());
            }
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        lock.lock();
        try {
            if (closed) return;
            closed = true;
            gc();
            funcCache = new HashMap<>();
            L.close();
        } finally {
            lock.unlock();
        }
    }

}
