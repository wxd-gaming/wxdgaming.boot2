package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.function.Function;

public class String2LongArray {

    public static final long[] EMPTY = new long[0];

    public static final Function<String, long[]> parse = new Function<String, long[]>() {
        @Override
        public long[] apply(String trim) {
            long[] arrays;
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parse(trim, long[].class);
                } else {
                    String[] split = trim.split("[，,|]");
                    arrays = new long[split.length];
                    for (int i = 0; i < split.length; i++) {
                        arrays[i] = Double.valueOf(split[i]).longValue();
                    }
                }
            } else {
                arrays = EMPTY;
            }
            return arrays;
        }
    };

}
