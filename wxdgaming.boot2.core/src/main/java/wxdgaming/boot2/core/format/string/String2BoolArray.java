package wxdgaming.boot2.core.format.string;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.function.FunctionUtil;
import wxdgaming.boot2.core.json.FastJsonUtil;

public class String2BoolArray {

    public static final boolean[] EMPTY = new boolean[0];

    public static boolean[] parse(String trim) {
        boolean[] arrays;
        trim = trim.replace('|', ',');
        if (StringUtils.isNotBlank(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parse(trim, boolean[].class);
            } else {
                JSONArray jsonArray = FunctionUtil.split2JSONArray(trim, ",");
                arrays = FunctionUtil.jsonArray2BooleanArray.apply(jsonArray);
            }
        } else {
            arrays = EMPTY;
        }
        return arrays;
    }
}
