package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.ArrayList;
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
                    String[] split = trim.split("[;]");
                    arrays = new ArrayList<>(split.length);
                    for (int i = 0; i < split.length; i++) {
                        String[] split2 = split[i].split("[,，|]");
                        arrays.add(split2);
                    }
                }
            } else {
                arrays = List.of();
            }
            return arrays;
        }
    };

}
