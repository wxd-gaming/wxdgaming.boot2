package wxdgaming.boot2.core.format.string;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.function.Function;

public class String2ByteArray {

    public static final Function<String, byte[]> parse = new Function<String, byte[]>() {
        @Override
        public byte[] apply(String trim) {
            byte[] arrays;
            if (StringUtils.isNotBlank(trim)) {
                trim = trim.replace('|', ',');
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    arrays = FastJsonUtil.parse(trim, byte[].class);
                } else {
                    String[] split = trim.split("[，,|]");
                    arrays = new byte[split.length];
                    for (int i = 0; i < split.length; i++) {
                        arrays[i] = Double.valueOf(split[i]).byteValue();
                    }
                }
            } else {
                arrays = Const.EMPTY_BYTE_ARRAY;
            }
            return arrays;
        }
    };

}
