package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.function.FunctionUtil;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class String2IntArrayList {

    public static final Function<String, List<int[]>> parse = new Function<String, List<int[]>>() {

        @Override
        public List<int[]> apply(String trim) {
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    return FastJsonUtil.parseArray(trim, int[].class);
                } else {
                    return FunctionUtil.split2ListIntArray(trim, ";", ",");
                }
            } else {
                return Collections.emptyList();
            }
        }
    };

}
