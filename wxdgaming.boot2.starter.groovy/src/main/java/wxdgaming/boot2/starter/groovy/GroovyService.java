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

    /**
     * 执行并返回结果
     *
     * @param script 脚本源码
     * @return 执行后的返回值
     */
    public Object evaluate(String script) {
        return shell.evaluate(script);
    }

    /**
     * 从指定目录加载文件并且执行
     *
     * @param path 目录
     * @return 执行后的返回值
     */
    public Object evaluateByResource(String path) {
        String script = FileReadUtil.readString(path, StandardCharsets.UTF_8);
        return shell.evaluate(script);
    }

    /**
     * 从指定目录加载文件并且执行，主要这里会缓存加载的脚本
     *
     * @param path       文件目录
     * @param methodName 需要执行的方法名
     * @param args       方法参数
     * @return 执行后的返回值
     */
    public Object invokeMethodWithCacheByResource(String path, String methodName, Object... args) {
        Script script = parseScriptCacheByResource(path);
        return script.invokeMethod(methodName, args);
    }


    /**
     * 从指定目录加载文件并且执行
     *
     * @param path       文件目录
     * @param methodName 需要执行的方法名
     * @param args       方法参数
     * @return 执行后的返回值
     */
    public Object invokeMethodWithByResource(String path, String methodName, Object... args) {
        Script script = parseScriptByResource(path);
        return script.invokeMethod(methodName, args);
    }

    /**
     * 根据资源读取缓存，如果缓存不存在加载一次
     *
     * @param path 文件目录
     * @return script
     */
    public Script parseScriptCacheByResource(String path) {
        return scriptCaches.computeIfAbsent(path, this::parseScriptByResource);
    }

    /**
     * 根据资源读取加载成脚本
     *
     * @param path 文件目录
     * @return script
     */
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
