package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.function.FunctionUtil;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class String2LongList {

    public static final Function<String, List<Long>> parse = new Function<String, List<Long>>() {
        @Override
        public List<Long> apply(String trim) {
            List<Long> arrays;
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parseArray(trim, Long.class);
                } else {
                    arrays = FunctionUtil.split2List(trim, ",", str -> Double.valueOf(str).longValue());
                }
            } else {
                arrays = Collections.emptyList();
            }
            return arrays;
        }
    };

}
