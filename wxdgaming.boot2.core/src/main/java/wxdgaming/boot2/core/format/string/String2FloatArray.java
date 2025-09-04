package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.function.Function;

public class String2FloatArray {

    public static final float[] EMPTY = new float[0];

    public static final Function<String, float[]> parse = new Function<String, float[]>() {
        @Override
        public float[] apply(String trim) {
            float[] arrays;
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parse(trim, float[].class);
                } else {
                    String[] split = trim.split("[，,|]");
                    arrays = new float[split.length];
                    for (int i = 0; i < split.length; i++) {
                        arrays[i] = Double.valueOf(split[i]).floatValue();
                    }
                }
            } else {
                arrays = EMPTY;
            }
            return arrays;
        }
    };

}
