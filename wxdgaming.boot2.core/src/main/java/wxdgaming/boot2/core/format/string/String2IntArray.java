package wxdgaming.boot2.core.format.string;

import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

public class String2IntArray {

    public static final int[] EMPTY = new int[0];

    public static int[] parse(String trim) {
        int[] arrays;
        if (StringUtils.isNotBlank(trim)) {
            trim = trim.replace('|', ',');
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parse(trim, int[].class);
            } else {
                String[] split = trim.split("[，,|:]");
                arrays = new int[split.length];
                for (int i = 0; i < split.length; i++) {
                    arrays[i] = Double.valueOf(split[i]).intValue();
                }
            }
        } else {
            arrays = EMPTY;
        }
        return arrays;
    }
}
