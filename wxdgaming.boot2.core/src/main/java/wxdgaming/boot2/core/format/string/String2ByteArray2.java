package wxdgaming.boot2.core.format.string;

import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

/**
 * 把 string 转化成 byte[][]
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-10-12 21:14
 */
public class String2ByteArray2 {

    public static final byte[][] EMPTY = new byte[0][];

    /**
     * 把 string 转化成 byte[][]
     *
     * @param trim 字符
     * @return byte[][]
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2024-10-12 21:14
     */
    public static byte[][] parse(String trim) {
        byte[][] arrays;
        trim = trim.replace('|', ',');
        if (StringUtils.isNotBlank(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parse(trim, byte[][].class);
            } else {
                String[] split = trim.split("[;]");
                arrays = new byte[split.length][];
                for (int i = 0; i < split.length; i++) {
                    String[] split2 = split[i].split("[,，|]");
                    byte[] vs1 = new byte[split2.length];
                    for (int i1 = 0; i1 < split2.length; i1++) {
                        vs1[i1] = Double.valueOf(split2[i1]).byteValue();
                    }
                    arrays[i] = vs1;
                }
            }
        } else {
            arrays = EMPTY;
        }
        return arrays;
    }
}
