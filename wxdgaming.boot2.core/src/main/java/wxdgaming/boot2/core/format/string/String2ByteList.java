package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.List;

public class String2ByteList {

    public static List<Byte> parse(String trim) {
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
            arrays = List.of();
        }
        return arrays;
    }
}
