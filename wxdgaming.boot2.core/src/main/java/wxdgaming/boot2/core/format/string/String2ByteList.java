package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class String2ByteList {

    public static final Function<String, List<Byte>> parse = new Function<String, List<Byte>>() {
        @Override public List<Byte> apply(String trim) {
            List<Byte> arrays;
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parseArray(trim, Byte.class);
                } else {
                    String[] split = trim.split("[，,|]");
                    arrays = new ArrayList<>(split.length);
                    for (int i = 0; i < split.length; i++) {
                        arrays.add(Double.valueOf(split[i]).byteValue());
                    }
                }
            } else {
                arrays = Collections.emptyList();
            }
            return arrays;
        }
    };

}
