package wxdgaming.boot2.core.format.string;

import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

public class String2FloatArray {

    public static final float[] EMPTY = new float[0];

    public static float[] parse(String trim) {
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
}
