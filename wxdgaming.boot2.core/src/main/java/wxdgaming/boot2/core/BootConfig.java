package wxdgaming.boot2.core;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.executor.ExecutorConfig;
import wxdgaming.boot2.core.io.FileUtil;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.util.JvmUtil;
import wxdgaming.boot2.core.util.YamlUtil;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * 启动配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 15:55
 **/
@Getter
public class BootConfig {

    @Getter private static final BootConfig ins = new BootConfig();

    private BootConfig() {}

    public void loadConfig() throws Exception {
        String property = JvmUtil.getProperty("boot.config", "boot.yml", s -> s);
        Tuple2<Path, byte[]> inputStream = FileUtil.findInputStream(BootConfig.class.getClassLoader(), property);
        if (property.endsWith(".json")) {
            String json = new String(inputStream.getRight(), StandardCharsets.UTF_8);
            config = JSONObject.parseObject(json);
        } else {
            config = YamlUtil.loadYaml(new ByteArrayInputStream(inputStream.getRight()));
        }
    }

    private JSONObject config = new JSONObject(true);

    public <R> R value(Value value, Type type) {
        /*实现注入*/
        String path = value.path();
        R r;
        try {
            if (value.nestedPath()) {
                r = BootConfig.getIns().getNestedValue(path, type);
            } else {
                r = BootConfig.getIns().getObject(path, type);
            }
            if (type instanceof Class<?> clazz && clazz.isInstance(r)) {
                return (R) clazz.cast(r);
            }
            if (r == null && !value.defaultValue().isBlank()) {
                r = FastJsonUtil.parse(value.defaultValue(), type);
            }
        } catch (Exception e) {
            throw Throw.of("参数：" + path, e);
        }
        if (value.required() && r == null) {
            throw new RuntimeException("value:" + path + " is null");
        }
        return r;
    }

    public boolean isDebug() {
        return config.getBooleanValue("debug");
    }

    public int gid() {
        Integer sid = config.getInteger("gid");
        if (sid == null) {
            throw new RuntimeException("gid is null");
        }
        return sid;
    }

    public int sid() {
        Integer sid = config.getInteger("sid");
        if (sid == null) {
            throw new RuntimeException("sid is null");
        }
        return sid;
    }

    public String sname() {
        String sname = config.getString("sname");
        if (sname == null) {
            return "boot server";
        }
        return sname;
    }

    public ExecutorConfig basicConfig() {
        return getNestedValue("executor.basic", ExecutorConfig.class, ExecutorConfig.DEFAULT_INSTANCE);
    }

    public ExecutorConfig logicConfig() {
        return getNestedValue("executor.logic", ExecutorConfig.class, ExecutorConfig.LOGIC_INSTANCE);
    }

    public ExecutorConfig virtualConfig() {
        return getNestedValue("executor.virtual", ExecutorConfig.class, ExecutorConfig.VIRTUAL_INSTANCE);
    }

    public int getIntValue(String key) {
        return config.getIntValue(key);
    }

    public Integer getInteger(String key) {
        return config.getInteger(key);
    }

    public Long getLong(String key) {
        return config.getLong(key);
    }

    public long getLongValue(String key) {
        return config.getLongValue(key);
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public <T> T getObject(String key, Type clazz) {
        return config.getObject(key, clazz);
    }

    public <T> T getObject(String key, Type clazz, Object defaultValue) {
        return FastJsonUtil.getObject(config, key, clazz, defaultValue);
    }

    public <T> T getNestedValue(String path, Type clazz) {
        return FastJsonUtil.getNestedValue(config, path, clazz, null);
    }

    /** 泛型方法：通过路由获取嵌套的 JSON 数据并转换为指定类型 */
    public <T> T getNestedValue(String path, Type clazz, Object defaultValue) {
        return FastJsonUtil.getNestedValue(config, path, clazz, defaultValue);
    }


}
