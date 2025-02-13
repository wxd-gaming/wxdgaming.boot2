package wxdgaming.boot2.core.format.string;

import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

public class String2BoolArray {

    public static final boolean[] EMPTY = new boolean[0];

    public static boolean[] parse(String trim) {
        boolean[] arrays;
        trim = trim.replace('|', ',');
        if (StringUtils.isNotBlank(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parse(trim, boolean[].class);
            } else {
                String[] split = trim.split("[，,|]");
                arrays = new boolean[split.length];
                for (int i = 0; i < split.length; i++) {
                    arrays[i] = Boolean.parseBoolean(split[i]);
                }
            }
        } else {
            arrays = EMPTY;
        }
        return arrays;
    }
}
