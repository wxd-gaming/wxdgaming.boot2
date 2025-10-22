package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.function.FunctionUtil;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class String2LongArrayList {

    public static final Function<String, List<long[]>> parse = new Function<String, List<long[]>>() {
        @Override
        public List<long[]> apply(String trim) {
            List<long[]> arrays;
            if (StringUtils.isNotBlank(trim)) {
                trim = trim.replace('|', ',');
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parseArray(trim, long[].class);
                } else {
                    arrays = FunctionUtil.split2ListJSONArray.andThen(FunctionUtil.listJsonArray2ListLongArray).apply(trim, ";", ",");
                }
            } else {
                arrays = Collections.emptyList();
            }
            return arrays;
        }
    };

}
