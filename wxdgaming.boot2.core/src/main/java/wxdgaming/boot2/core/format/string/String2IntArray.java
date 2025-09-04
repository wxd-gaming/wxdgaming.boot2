package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.function.Function;

public class String2IntArray {


    public static final Function<String, int[]> parse = new Function<String, int[]>() {
        @Override
        public int[] apply(String trim) {
            int[] arrays;
            if (StringUtils.isNotBlank(trim)) {
                trim = trim.replace('|', ',');
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parse(trim, int[].class);
                } else {
                    String[] split = trim.split("[，,|:]");
                    arrays = new int[split.length];
                    for (int i = 0; i < split.length; i++) {
                        arrays[i] = Double.valueOf(split[i]).intValue();
                    }
                }
            } else {
                arrays = Const.EMPTY_INT_ARRAY;
            }
            return arrays;
        }
    };

}
