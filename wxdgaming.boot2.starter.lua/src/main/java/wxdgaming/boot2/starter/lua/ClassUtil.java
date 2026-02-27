package wxdgaming.boot2.starter.lua;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassUtil {

    static List<String> resources = null;

    @SneakyThrows public static List<String> getResources(ClassLoader classLoader) {
        if (resources == null) {
            InputStream resourceAsStream = classLoader.getResourceAsStream("resources.json");
            if (resourceAsStream != null) {
                byte[] bytes = IOUtils.toByteArray(resourceAsStream);
                String string = new String(bytes, StandardCharsets.UTF_8);
                resources = JSON.parseObject(string, new TypeReference<ArrayList<String>>() {});
            }
        }

        if (resources == null) {
            resources = Collections.emptyList();
        }

        return resources;
    }

}
