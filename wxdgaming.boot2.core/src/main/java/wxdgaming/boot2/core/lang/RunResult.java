package wxdgaming.boot2.core.lang;

import com.alibaba.fastjson.JSONObject;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

import java.util.Map;

/**
 * 执行
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 10:49
 **/
public class RunResult extends JSONObject {
    public static RunResult parse(String json) {
        return FastJsonUtil.parse(json, RunResult.class);
    }

    public static RunResult ok() {
        return new RunResult().fluentPut("code", 1).fluentPut("msg", "ok");
    }

    public static RunResult error(String message) {
        return error(99, message);
    }

    public static RunResult error(int code, String message) {
        return new RunResult().fluentPut("code", code).fluentPut("msg", message);
    }

    public RunResult() {
        super(true);
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

    @Override public RunResult fluentPut(String key, Object value) {
        super.fluentPut(key, value);
        return this;
    }

    @Override public RunResult fluentPutAll(Map<? extends String, ?> m) {
        super.fluentPutAll(m);
        return this;
    }
}
