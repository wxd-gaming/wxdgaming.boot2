package wxdgaming.boot2.core.chatset.json;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson2.JSONWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * long 类型的序列化和反序列化
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 15:24
 **/
public class LongSerializerFastJson implements ObjectSerializer, ObjectDeserializer {

    public static final LongSerializerFastJson default_instance = new LongSerializerFastJson();

    @Override public Long deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object parse = parser.parse();
        if (parse instanceof Long) {
            return (Long) parse;
        }
        return Long.parseLong(parse.toString());
    }

    @Override public void write(JSONWriter jsonWriter, Object object) {
        ObjectSerializer.super.write(jsonWriter, object);
    }

    @Override public void writeJSONB(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        ObjectSerializer.super.writeJSONB(jsonWriter, object, fieldName, fieldType, features);
    }

    @Override public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        long v = (Long) object;
        serializer.writeLong(v);
    }

    @Override public long getFeatures() {
        return ObjectSerializer.super.getFeatures();
    }
}
