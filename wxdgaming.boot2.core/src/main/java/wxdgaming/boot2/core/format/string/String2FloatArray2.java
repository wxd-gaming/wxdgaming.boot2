package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.function.Function;

public class String2FloatArray2 {

    public static final float[][] EMPTY = new float[0][];

    public static final Function<String, float[][]> parse = new Function<String, float[][]>() {
        @Override
        public float[][] apply(String trim) {
            float[][] arrays;
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parse(trim, float[][].class);
                } else {
                    String[] split = trim.split("[;]");
                    arrays = new float[split.length][];
                    for (int i = 0; i < split.length; i++) {
                        String[] split2 = split[i].split("[,，|]");
                        float[] vs1 = new float[split2.length];
                        for (int i1 = 0; i1 < split2.length; i1++) {
                            vs1[i1] = Double.valueOf(split2[i1]).floatValue();
                        }
                        arrays[i] = vs1;
                    }
                }
            } else {
                arrays = EMPTY;
            }
            return arrays;
        }
    };

}
