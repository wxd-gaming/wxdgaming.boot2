package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class String2IntArrayList {

    public static final Function<String, List<int[]>> parse = new Function<String, List<int[]>>() {

        @Override
        public List<int[]> apply(String trim) {
            List<int[]> arrays;
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parseArray(trim, int[].class);
                } else {
                    String[] split = trim.split("[;]");
                    arrays = new ArrayList<>(split.length);
                    for (int i = 0; i < split.length; i++) {
                        String[] split2 = split[i].split("[,，|]");
                        int[] vs1 = new int[split2.length];
                        for (int i1 = 0; i1 < split2.length; i1++) {
                            vs1[i1] = Double.valueOf(split2[i1]).intValue();
                        }
                        arrays.add(vs1);
                    }
                }
            } else {
                arrays = List.of();
            }
            return arrays;
        }
    };

}
