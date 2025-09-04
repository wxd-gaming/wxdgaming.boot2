package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class String2FloatArrayList {

    public static final Function<String, List<float[]>> parse = new Function<String, List<float[]>>() {
        @Override public List<float[]> apply(String trim) {

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
                arrays = Collections.emptyList();
            }
            return arrays;
        }
    };
}
