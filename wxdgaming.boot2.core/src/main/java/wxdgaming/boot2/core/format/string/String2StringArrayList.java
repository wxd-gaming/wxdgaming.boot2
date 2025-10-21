package wxdgaming.boot2.core.format.string;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.function.FunctionUtil;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class String2StringArrayList {


    public static final Function<String, List<String[]>> parse = new Function<String, List<String[]>>() {
        @Override public List<String[]> apply(String trim) {
            List<String[]> arrays;
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parseArray(trim, String[].class);
                } else {
                    List<JSONArray> jsonArrays = FunctionUtil.split2ListJSONArray(trim, ";", ",");
                    arrays = FunctionUtil.listJsonArray2ListStringArray.apply(jsonArrays);
                }
            } else {
                arrays = Collections.emptyList();
            }
            return arrays;
        }
    };

}
