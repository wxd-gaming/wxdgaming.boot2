package wxdgaming.boot2.starter.js;

import lombok.Getter;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.io.FileReadUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * js脚本服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-03-04 20:22
 **/
@Getter
@Service
public class JsService extends HoldApplicationContext {

    private final List<Consumer<JSContext>> onInitListener = new ArrayList<>();
    private ConcurrentHashMap<Thread, JSContext> threadJsContext = new ConcurrentHashMap<>();

    @Init
    public void init() {
        /*重构插件*/
        clear();
    }

    public JSContext threadContext() {
        return threadJsContext.computeIfAbsent(Thread.currentThread(), k -> {
            JSContext jsContext = JSContext.build();
            /*构建插件*/
            if (applicationContextProvider != null) {
                applicationContextProvider.classWithSuperStream(IJSPlugin.class).forEach(jsContext::put);
            }
            onInitListener.forEach(jsPlugin -> jsPlugin.accept(jsContext));
            return jsContext;
        });
    }

    public void clear() {
        ConcurrentHashMap<Thread, JSContext> threadJsContext1 = threadJsContext;
        threadJsContext = new ConcurrentHashMap<>();
        threadJsContext1.values().forEach(JSContext::close);
    }

    public void release() {
        threadJsContext.remove(Thread.currentThread());
    }

    public Value evalFile(String file) {
        JSContext context = threadContext();
        String string = FileReadUtil.readString(file, StandardCharsets.UTF_8);
        return context.eval(string);
    }

    public Value eval(String script) {
        JSContext context = threadContext();
        return context.eval(script);
    }

}
