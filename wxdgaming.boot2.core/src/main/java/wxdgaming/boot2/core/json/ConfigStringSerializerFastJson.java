package wxdgaming.boot2.core.json;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import wxdgaming.boot2.core.lang.ConfigString;

import java.lang.reflect.Type;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2022-04-21 10:11
 **/
public class ConfigStringSerializerFastJson implements ObjectWriter<ConfigString>, ObjectReader<ConfigString> {

    public static final ConfigStringSerializerFastJson default_instance = new ConfigStringSerializerFastJson();

    @Override public ConfigString readObject(JSONReader jsonReader, Type type, Object o, long l) {
        Object parse = jsonReader.readAny();
        if (parse instanceof ConfigString configString) return configString;
        if (parse instanceof JSONObject jsonObject) {
            String value = jsonObject.getString("value");
            return new ConfigString(value);
        }
        return new ConfigString("");
    }

    @Override public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        ConfigString configString = (ConfigString) object;
        jsonWriter.writeString(configString.getValue());
    }

    @Override public long getFeatures() {
        return 0;
    }
}
