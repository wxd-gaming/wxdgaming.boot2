package wxdgaming.boot2.core.util;

import wxdgaming.boot2.core.io.Objects;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CDKeyUtil {

    private static final int[] AES_KEY_ENCODE = {1, 2, 5, 3, 2};
    private static final int[] AES_KEY_DECODE = Objects.reverse(AES_KEY_ENCODE);


    /* key算法 12位id | 42位时间戳 | 9位自增 */
    public static List<String> cdKey(long cdKeyId, int num) {
        return cdKey(AES_KEY_ENCODE, cdKeyId, num);
    }

    public static List<String> cdKey(int[] encode, long cdKeyId, int num) {
        AssertUtil.assertTrue(num <= 511, "cdKeyId 最大生成 511 个");
        AssertUtil.assertTrue(cdKeyId < 4095, "cdKeyId 最大 4095");
        cdKeyId = cdKeyId << 51;
        /*时间戳用42位*/
        long second = System.currentTimeMillis();
        cdKeyId = cdKeyId | second << 9;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            cdKeyId += 1;
            BigInteger bigInteger = new BigInteger(String.valueOf(cdKeyId));
            String string = bigInteger.toString(36);
            String upperCase = string.toUpperCase();
            upperCase = AesUtil.convert_ASE(upperCase, encode);
            list.add(upperCase);
        }
        return list;
    }

    public static int getCdKeyId(String cdKey) {
        return getCdKeyId(AES_KEY_DECODE, cdKey);
    }

    public static int getCdKeyId(int[] decode, String cdKey) {
        cdKey = AesUtil.convert_ASE(cdKey, decode);
        BigInteger bigInteger = new BigInteger(cdKey, 36);
        long longValue = bigInteger.longValue();
        long cdKeyId = longValue >> 51;
        return (int) cdKeyId;
    }

}
