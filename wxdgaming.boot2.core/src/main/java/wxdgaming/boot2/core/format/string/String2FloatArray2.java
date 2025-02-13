package wxdgaming.boot2.core.format.string;

import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

public class String2FloatArray2 {

    public static final float[][] EMPTY = new float[0][];

    public static float[][] parse(String trim) {
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
}
