package wxdgaming.boot2.core.lang;

import com.alibaba.fastjson.JSONObject;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 执行
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 10:49
 **/
public class RunResult extends JSONObject {

    /** 不可变更的 */
    public static final RunResult OK = new RunResult(Collections.unmodifiableMap(ok()));

    public static RunResult parse(String json) {
        return FastJsonUtil.parse(json, RunResult.class);
    }

    public static RunResult ok() {
        return new RunResult().fluentPut("code", 1).fluentPut("msg", "ok");
    }

    public static RunResult fail(String message) {
        return fail(99, message);
    }

    public static RunResult fail(int code, String message) {
        return new RunResult().fluentPut("code", code).fluentPut("msg", message);
    }

    public RunResult() {
        super(true);
    }

    public RunResult(Map<String, Object> map) {
        super(map);
    }

    public boolean isOk() {
        return code() == 1;
    }

    public boolean isFail() {
        return code() != 1;
    }

    public int code() {
        return getIntValue("code");
    }

    public RunResult code(int code) {
        put("code", code);
        return this;
    }

    public String msg() {
        return getString("msg");
    }

    public RunResult msg(String message) {
        put("msg", message);
        return this;
    }

    public JSONObject data() {
        return getJSONObject("data");
    }

    public <R> R data(Class<R> clazz) {
        return getObject("data", clazz);
    }

    public RunResult data(Object data) {
        put("data", data);
        return this;
    }

    public <T> T getObject(String key, Class<T> clazz, Supplier<Object> supplier) {
        return FastJsonUtil.getObject(this, key, clazz, supplier);
    }

    /** 泛型方法：通过路由获取嵌套的 JSON 数据并转换为指定类型 */
    public <T> T getNestedValue(String path, Class<T> clazz) {
        return FastJsonUtil.getNestedValue(this, path, clazz, null);
    }

    /** 泛型方法：通过路由获取嵌套的 JSON 数据并转换为指定类型 */
    public <T> T getNestedValue(String path, Class<T> clazz, Supplier<Object> supplier) {
        return FastJsonUtil.getNestedValue(this, path, clazz, supplier);
    }

    @Override public RunResult fluentPut(String key, Object value) {
        super.fluentPut(key, value);
        return this;
    }

    @Override public RunResult fluentPutAll(Map<? extends String, ?> m) {
        super.fluentPutAll(m);
        return this;
    }
}
