package wxdgaming.boot2.core.format.string;

import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class String2IntList {

    public static final Function<String, List<Integer>> parse = new Function<String, List<Integer>>() {
        @Override
        public List<Integer> apply(String trim) {
            List<Integer> arrays;
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parseArray(trim, Integer.class);
                } else {
                    String[] split = trim.split("[，,|]");
                    arrays = new ArrayList<>(split.length);
                    for (int i = 0; i < split.length; i++) {
                        arrays.add(Double.valueOf(split[i]).intValue());
                    }
                }
            } else {
                arrays = List.of();
            }
            return arrays;
        }
    };

}
