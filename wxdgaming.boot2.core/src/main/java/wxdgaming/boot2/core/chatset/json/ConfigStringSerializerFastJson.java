package wxdgaming.boot2.core.chatset.json;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.lang.ConfigString;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-04-21 10:11
 **/
public class ConfigStringSerializerFastJson implements ObjectSerializer, ObjectDeserializer {

    public static final ConfigStringSerializerFastJson default_instance = new ConfigStringSerializerFastJson();

    @Override
    public void write(JSONSerializer serializer,
                      Object object,
                      Object fieldName,
                      Type fieldType,
                      int features) throws IOException {
        ConfigString configString = (ConfigString) object;
        serializer.write(configString.getValue());
    }

    /** 由于集合序列化后为子类,再进行反序列化时,无法还原原对象,需要修改反序列化方法,手动修改反序列化逻辑 */
    @Override
    public Object deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object parse = parser.parse();
        if (parse instanceof ConfigString configString) return configString;
        if (parse instanceof JSONObject jsonObject) {
            String value = jsonObject.getString("value");
            if (StringUtils.isNotBlank(value)) {
                return new ConfigString(value);
            }
        }

        return new ConfigString(parse.toString());
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }

    @Override public long getFeatures() {
        return ObjectSerializer.super.getFeatures();
    }
}
