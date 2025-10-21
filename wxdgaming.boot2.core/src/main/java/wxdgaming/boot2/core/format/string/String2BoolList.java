package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.function.FunctionUtil;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class String2BoolList {

    public static final Function<String, List<Boolean>> parse = new Function<String, List<Boolean>>() {
        @Override
        public List<Boolean> apply(String trim) {
            List<Boolean> arrays;
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parseArray(trim, Boolean.class);
                } else {
                    arrays = FunctionUtil.split2List(trim, ",", Boolean::parseBoolean);
                }
            } else {
                arrays = Collections.emptyList();
            }
            return arrays;
        }
    };

}
