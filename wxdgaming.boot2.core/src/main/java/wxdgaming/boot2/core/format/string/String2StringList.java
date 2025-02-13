package wxdgaming.boot2.core.format.string;

import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

import java.util.List;

public class String2StringList {

    public static List<String> parse(String trim) {
        List<String> arrays;
        trim = trim.replace('|', ',');
        if (StringUtils.isNotBlank(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parseArray(trim, String.class);
            } else {
                String[] split = trim.split("[，,|]");
                arrays = List.of(split);
            }
        } else {
            arrays = List.of();
        }
        return arrays;
    }
}
