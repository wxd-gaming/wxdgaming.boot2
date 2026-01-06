package wxdgaming.boot2.core.json;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import wxdgaming.boot2.core.lang.bit.BitFlag;

import java.lang.reflect.Type;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2022-04-21 10:11
 **/
public class BitFlagSerializerFastJson implements ObjectWriter<BitFlag>, ObjectReader<BitFlag> {

    public static final BitFlagSerializerFastJson default_instance = new BitFlagSerializerFastJson();

    @Override public long getFeatures() {
        return 0;
    }

    @Override public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        jsonWriter.writeInt64(((BitFlag) object).getLongs());
    }

    @Override
    public BitFlag readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        long[] ls = jsonReader.read(long[].class);
        return new BitFlag(ls);
    }
}
