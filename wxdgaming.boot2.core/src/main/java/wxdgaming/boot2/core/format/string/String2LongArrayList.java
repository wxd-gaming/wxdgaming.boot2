package wxdgaming.boot2.core.format.string;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.function.FunctionUtil;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class String2LongArrayList {

    public static final Function<String, List<long[]>> parse = new Function<String, List<long[]>>() {
        @Override
        public List<long[]> apply(String trim) {
            List<long[]> arrays;
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parseArray(trim, long[].class);
                } else {
                    List<JSONArray> jsonArrays = FunctionUtil.split2ListJSONArray(trim, ";", ",");
                    arrays = new ArrayList<>(jsonArrays.size());
                    for (JSONArray jsonArray : jsonArrays) {
                        arrays.add(FunctionUtil.jsonArray2LongArray.apply(jsonArray));
                    }
                }
            } else {
                arrays = Collections.emptyList();
            }
            return arrays;
        }
    };

}
