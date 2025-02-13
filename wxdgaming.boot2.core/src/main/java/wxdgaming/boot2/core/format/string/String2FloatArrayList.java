package wxdgaming.boot2.core.format.string;

import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.List;

public class String2FloatArrayList {

    public static List<float[]> parse(String trim) {
        List<float[]> arrays;
        trim = trim.replace('|', ',');
        if (StringUtils.isNotBlank(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parseArray(trim, float[].class);
            } else {
                String[] split = trim.split("[;]");
                arrays = new ArrayList<>(split.length);
                for (int i = 0; i < split.length; i++) {
                    String[] split2 = split[i].split("[,，|]");
                    float[] vs1 = new float[split2.length];
                    for (int i1 = 0; i1 < split2.length; i1++) {
                        vs1[i1] = Double.valueOf(split2[i1]).floatValue();
                    }
                    arrays.add(vs1);
                }
            }
        } else {
            arrays = List.of();
        }
        return arrays;
    }
}
