package wxdgaming.boot2.core.util;

import com.alibaba.fastjson.JSONObject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.io.FileUtil;
import wxdgaming.boot2.core.lang.Tuple2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * yaml文件读取
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-04-23 17:23
 **/
public class YamlUtil {

    public static JSONObject loadYaml(String path) {
        return loadYaml(Thread.currentThread().getContextClassLoader(), path);
    }

    public static JSONObject loadYaml(ClassLoader classLoader, String path) {
        try {
            Tuple2<Path, byte[]> inputStream = FileUtil.findInputStream(classLoader, path);
            return loadYaml(new ByteArrayInputStream(inputStream.getRight()));
        } catch (Exception e) {
            throw new RuntimeException("读取文件：" + path, e);
        }
    }

    public static JSONObject loadYaml(InputStream inputStream) {
        DumperOptions dumperOptions = new DumperOptions();
        Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(representer, dumperOptions);
        JSONObject jsonObject = yaml.loadAs(inputStream, JSONObject.class);
        return FastJsonUtil.parse(jsonObject.toJSONString());
    }

    /** 把指定类型转换成 yaml 文件 */
    public static String dumpYaml(Object source) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(representer, dumperOptions);
        return yaml.dumpAsMap(source);
    }

}
