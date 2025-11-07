package wxdgaming.boot2.starter.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.io.FileReadUtil;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-03 16:47
 **/
@Getter
@Service
public class GroovyService extends HoldApplicationContext {

    private int version = 1;
    final GroovyShell shell = new GroovyShell();
    final ConcurrentHashMap<String, GroovyHandler> handlerCaches = new ConcurrentHashMap<>();
    final ConcurrentHashMap<String, Script> scriptCaches = new ConcurrentHashMap<>();

    public Object evaluate(String script) {
        return shell.evaluate(script);
    }

    public Object invokeMethodWithCacheByResource(String path, String methodName, Object... args) {
        Script script = parseScriptCacheByResource(path);
        return script.invokeMethod(methodName, args);
    }

    public Script parseScriptCacheByResource(String path) {
        return scriptCaches.computeIfAbsent(path, this::parseScriptByResource);
    }

    public Object invokeMethodWithByResource(String path, String methodName, Object... args) {
        Script script = parseScriptByResource(path);
        return script.invokeMethod(methodName, args);
    }

    public Script parseScriptByResource(String path) {
        String script = FileReadUtil.readString(path, StandardCharsets.UTF_8);
        return shell.parse(script);
    }

    public Script parseScript(String script) {
        return shell.parse(script);
    }

    public GroovyHandler handlerCacheByResource(String path) {
        return handlerCaches.computeIfAbsent(path, this::loadHandlerByResource);
    }

    public GroovyHandler loadHandlerByResource(String path) {
        String script = FileReadUtil.readString(path, StandardCharsets.UTF_8);
        return loadHandler(script);
    }

    public GroovyHandler loadHandler(String script) {
        try {
            GroovyClassLoader classLoader = new GroovyClassLoader();
            Class aClass = classLoader.parseClass(script);
            Object instance = aClass.getDeclaredConstructor().newInstance();
            if (instance instanceof GroovyHandler handler) {
                return handler;
            }
            throw new RuntimeException("not a GroovyHandler");
        } catch (Exception e) {
            throw new RuntimeException("not a GroovyHandler", e);
        }
    }


}
