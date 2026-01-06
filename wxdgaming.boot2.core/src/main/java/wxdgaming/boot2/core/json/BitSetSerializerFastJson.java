package wxdgaming.boot2.core.json;


import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;

import java.lang.reflect.Type;
import java.util.BitSet;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2022-04-21 10:11
 **/
public class BitSetSerializerFastJson implements ObjectWriter<BitSet>, ObjectReader<BitSet> {

    public static final BitSetSerializerFastJson default_instance = new BitSetSerializerFastJson();

    @Override public BitSet readObject(JSONReader jsonReader, Type type, Object o, long l) {
        long[] js = jsonReader.read(long[].class);
        return BitSet.valueOf(js);
    }

    @Override public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        BitSet bitSet = (BitSet) object;
        jsonWriter.writeInt64(bitSet.toLongArray());
    }

    @Override public long getFeatures() {
        return 0L;
    }
}
