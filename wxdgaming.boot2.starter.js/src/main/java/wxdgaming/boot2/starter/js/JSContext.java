package wxdgaming.boot2.starter.js;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import wxdgaming.boot2.core.io.FileReadUtil;
import wxdgaming.boot2.starter.js.plugin.JLog;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * js 容器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-03-04 09:12
 **/
public class JSContext {

    public static JSContext build() {
        return new JSContext();
    }

    private final Context context;

    private JSContext() {
        this.context = Context.newBuilder("js")
                .allowAllAccess(true)
                .allowHostClassLookup(className -> true)
                .build();
        JLog plugin = new JLog();
        put(plugin);
    }

    public void put(IJSPlugin jsPlugin) {
        this.context.getBindings("js").putMember(jsPlugin.getName(), jsPlugin);
    }

    public void put(String key, Object value) {
        this.context.getBindings("js").putMember(key, value);
    }

    public Value getMember(String key) {
        return this.context.getBindings("js").getMember(key);
    }

    public Value evalFile(String fileName) {
        String script = FileReadUtil.readString(fileName, StandardCharsets.UTF_8);
        return this.context.eval("js", script);
    }

    public Value evalFile(File file) {
        String script = FileReadUtil.readString(file, StandardCharsets.UTF_8);
        return this.context.eval("js", script);
    }


    public Value eval(String script) {
        return this.context.eval("js", script);
    }

    /**
     * 调用对象的函数
     *
     * @param identifier 函数名 比如 print() 这个就是print
     * @param arguments  传递的参数
     * @return 执行返回值
     */
    public Value execute(String identifier, Object... arguments) {
        Value __Member = getMember(identifier);
        return __Member.execute(arguments);
    }

    /**
     * 调用对象的函数
     *
     * @param member     对象，比如xxx
     * @param identifier 函数名 比如 xxx.print 这个就是print
     * @param arguments  传递的参数
     * @return 执行返回值
     */
    public Value invokeMember(String member, String identifier, Object... arguments) {
        Value __Member = getMember(member);
        return __Member.invokeMember(identifier, arguments);
    }

    public void close() {
        this.context.close(true);
    }

}
