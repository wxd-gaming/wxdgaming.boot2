package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class String2FloatList {

    public static final Function<String, List<Float>> parse = new Function<String, List<Float>>() {
        @Override
        public List<Float> apply(String trim) {
            List<Float> arrays;
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parseArray(trim, Float.class);
                } else {
                    String[] split = trim.split("[，,|]");
                    arrays = new ArrayList<>(split.length);
                    for (int i = 0; i < split.length; i++) {
                        arrays.add(Double.valueOf(split[i]).floatValue());
                    }
                }
            } else {
                arrays = Collections.emptyList();
            }
            return arrays;
        }
    };

}
