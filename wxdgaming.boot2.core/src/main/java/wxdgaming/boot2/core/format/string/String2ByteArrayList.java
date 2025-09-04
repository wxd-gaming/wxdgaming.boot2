package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class String2ByteArrayList {

    public static List<byte[]> parse(String trim) {
        List<byte[]> arrays;
        trim = trim.replace('|', ',');
        if (StringUtils.isNotBlank(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parseArray(trim, byte[].class);
            } else {
                String[] split = trim.split("[;]");
                arrays = new ArrayList<>(split.length);
                for (int i = 0; i < split.length; i++) {
                    String[] split2 = split[i].split("[,，|]");
                    byte[] vs1 = new byte[split2.length];
                    for (int i1 = 0; i1 < split2.length; i1++) {
                        vs1[i1] = Double.valueOf(split2[i1]).byteValue();
                    }
                    arrays.add(vs1);
                }
            }
        } else {
            arrays = Collections.emptyList();
        }
        return arrays;
    }
}
