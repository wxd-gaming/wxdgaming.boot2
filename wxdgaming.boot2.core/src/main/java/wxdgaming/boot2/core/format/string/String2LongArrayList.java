package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
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
                    String[] split = trim.split("[;]");
                    arrays = new ArrayList<>(split.length);
                    for (int i = 0; i < split.length; i++) {
                        String[] split2 = split[i].split("[,，|]");
                        long[] vs1 = new long[split2.length];
                        for (int i1 = 0; i1 < split2.length; i1++) {
                            vs1[i1] = Double.valueOf(split2[i1]).longValue();
                        }
                        arrays.add(vs1);
                    }
                }
            } else {
                arrays = Collections.emptyList();
            }
            return arrays;
        }
    };

}
