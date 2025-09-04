package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.function.Function;

public class String2LongArray2 {

    public static final long[][] EMPTY = new long[0][];

    public static final Function<String, long[][]> parse = new Function<String, long[][]>() {
        @Override
        public long[][] apply(String trim) {
            long[][] arrays;
            trim = trim.replace('|', ',');
            if (StringUtils.isNotBlank(trim)) {
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parse(trim, long[][].class);
                } else {
                    String[] split = trim.split("[;]");
                    arrays = new long[split.length][];
                    for (int i = 0; i < split.length; i++) {
                        String[] split2 = split[i].split("[,，|]");
                        long[] vs1 = new long[split2.length];
                        for (int i1 = 0; i1 < split2.length; i1++) {
                            vs1[i1] = Double.valueOf(split2[i1]).longValue();
                        }
                        arrays[i] = vs1;
                    }
                }
            } else {
                arrays = EMPTY;
            }
            return arrays;
        }
    };

}
