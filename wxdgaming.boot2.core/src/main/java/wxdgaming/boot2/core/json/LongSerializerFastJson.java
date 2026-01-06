package wxdgaming.boot2.core.json;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;

import java.lang.reflect.Type;

/**
 * long 类型的序列化和反序列化
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 15:24
 **/
public class LongSerializerFastJson implements ObjectWriter<Long>, ObjectReader<Long> {

    public static final LongSerializerFastJson default_instance = new LongSerializerFastJson();

    @Override public Long readObject(JSONReader jsonReader, Type type, Object o, long l) {
        Object parse = jsonReader.readAny();
        if (parse instanceof Long) {
            return (Long) parse;
        }
        return Long.parseLong(parse.toString());
    }

    @Override public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        long v = (Long) object;
        jsonWriter.writeInt64(v);
    }

    @Override public long getFeatures() {
        return 0;
    }
}
