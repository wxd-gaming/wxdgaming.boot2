package wxdgaming.boot2.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.Getter;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.executor.ExecutorConfig;
import wxdgaming.boot2.core.io.FileUtil;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.core.util.JvmUtil;
import wxdgaming.boot2.core.util.YamlUtil;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 启动配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 15:55
 **/
@Getter
public class BootConfig {

    private static final class Holder {
        private static final BootConfig ins = new BootConfig();
    }

    public static BootConfig getIns() {
        return Holder.ins;
    }

    private BootConfig() {}

    public void loadConfig() throws Exception {
        String property = JvmUtil.getProperty("boot.config", "boot.yml", s -> s);
        configNode = load0(property);
        Set<String> actives = getNestedValue("profiles.active", new TypeReference<Set<String>>() {}.getType(), Set::of);
        String profilesActive = System.getProperty("profiles.active");
        if (StringUtils.isNotBlank(profilesActive)) {
            String[] split = profilesActive.split(",");
            actives.addAll(Set.of(split));
        }
        for (String active : actives) {
            JSONObject activeJsonObject = load0("boot-%s.yml".formatted(active));
            Objects.mergeMaps(configNode, activeJsonObject);
        }
    }

    private JSONObject load0(String property) {
        Tuple2<Path, byte[]> inputStream = FileUtil.findInputStream(BootConfig.class.getClassLoader(), property);
        AssertUtil.assertNull(inputStream, "未找到配置文件：" + property);
        return YamlUtil.loadYaml(new ByteArrayInputStream(inputStream.getRight()));
    }

    private JSONObject configNode = new JSONObject(true);

    @SuppressWarnings("unchecked")
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
            if (r == null && StringUtils.isNotBlank(value.defaultValue())) {
                if (String.class.equals(type)) {
                    return (R) value.defaultValue();
                }
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
        return configNode.getBooleanValue("debug");
    }

    public int gid() {
        Integer sid = configNode.getInteger("gid");
        AssertUtil.assertNull(sid, "gid is null");
        return sid;
    }

    public int sid() {
        Integer sid = configNode.getInteger("sid");
        AssertUtil.assertNull(sid, "sid is null");
        return sid;
    }

    public String sname() {
        String sname = configNode.getString("sname");
        if (sname == null) {
            return "boot server";
        }
        return sname;
    }

    public ExecutorConfig basicConfig() {
        return getNestedValue("executor.basic", ExecutorConfig.class, ExecutorConfig.BASIC_INSTANCE);
    }

    public ExecutorConfig logicConfig() {
        return getNestedValue("executor.logic", ExecutorConfig.class, ExecutorConfig.LOGIC_INSTANCE);
    }

    public ExecutorConfig virtualConfig() {
        return getNestedValue("executor.virtual", ExecutorConfig.class, ExecutorConfig.VIRTUAL_INSTANCE);
    }

    public int getIntValue(String key) {
        return configNode.getIntValue(key);
    }

    public Integer getInteger(String key) {
        return configNode.getInteger(key);
    }

    public Long getLong(String key) {
        return configNode.getLong(key);
    }

    public long getLongValue(String key) {
        return configNode.getLongValue(key);
    }

    public String getString(String key) {
        return configNode.getString(key);
    }

    public <T> T getObject(String key, Type clazz) {
        return configNode.getObject(key, clazz);
    }

    public <T> T getObject(String key, Type clazz, Supplier<Object> supplier) {
        return FastJsonUtil.getObject(configNode, key, clazz, supplier);
    }

    public <T> T getNestedValue(String path, Type clazz) {
        return FastJsonUtil.getNestedValue(configNode, path, clazz, null);
    }

    /** 泛型方法：通过路由获取嵌套的 JSON 数据并转换为指定类型 */
    public <T> T getNestedValue(String path, Type clazz, Supplier<Object> supplier) {
        return FastJsonUtil.getNestedValue(configNode, path, clazz, supplier);
    }


}
