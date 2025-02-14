package wxdgaming.boot2.core;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import wxdgaming.boot2.core.io.FileUtil;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.threading.ExecutorConfig;
import wxdgaming.boot2.core.util.JvmUtil;

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
        String property = JvmUtil.getProperty("boot.config", "boot.json", s -> s);
        Tuple2<Path, byte[]> inputStream = FileUtil.findInputStream(BootConfig.class.getClassLoader(), property);
        String json = new String(inputStream.getRight(), StandardCharsets.UTF_8);
        config = JSONObject.parseObject(json);
    }

    private JSONObject config = new JSONObject(true);

    public boolean isDebug() {
        return config.getBooleanValue("debug");
    }

    public ExecutorConfig getExecutorConfig() {
        ExecutorConfig executor = config.getObject("executor", ExecutorConfig.class);
        if (executor == null) {
            executor = new ExecutorConfig();
        }
        return executor;
    }

    public int getIntValue(String key) {
        return config.getIntValue(key);
    }

    public int getInteger(String key) {
        return config.getInteger(key);
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public <T> T getObject(String key, Class<T> clazz) {
        return config.getObject(key, clazz);
    }

}
