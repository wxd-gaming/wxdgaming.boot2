package wxdgaming.boot2.core.json;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import wxdgaming.boot2.core.lang.TimeValue;

import java.lang.reflect.Type;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2022-04-21 10:11
 **/
public class TimeValueSerializerFastJson implements ObjectWriter<TimeValue>, ObjectReader<TimeValue> {

    public static final TimeValueSerializerFastJson default_instance = new TimeValueSerializerFastJson();

    @Override public TimeValue readObject(JSONReader jsonReader, Type type, Object o, long l) {
        Object parse = jsonReader.readAny();
        if (parse == null)
            return new TimeValue(0);
        if (parse instanceof Number number) {
            return new TimeValue(number.longValue());
        }
        return new TimeValue(parse.toString());
    }

    @Override public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        TimeValue bitSet = (TimeValue) object;
        jsonWriter.writeInt64(bitSet.getHold());
    }

    @Override public long getFeatures() {
        return 0;
    }
}
