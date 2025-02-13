package wxdgaming.boot2.core.format.string;

import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

public class String2StringArray {

    public static final String[] EMPTY = new String[0];

    public static String[] parse(String trim) {
        String[] arrays;
        trim = trim.replace('|', ',');
        if (StringUtils.isNotBlank(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parse(trim, String[].class);
            } else {
                String[] split = trim.split("[，,|]");
                arrays = new String[split.length];
                for (int i = 0; i < split.length; i++) {
                    arrays[i] = split[i];
                }
            }
        } else {
            arrays = EMPTY;
        }
        return arrays;
    }
}
