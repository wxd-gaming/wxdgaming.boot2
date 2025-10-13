package wxdgaming.boot2.core.util;

import org.apache.commons.lang3.RandomStringUtils;
import wxdgaming.boot2.core.io.Objects;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;

public class CDKeyUtil {

    private static final int[] AES_KEY_ENCODE = {1, 4, 3, 6, 2};
    private static final int[] AES_KEY_DECODE = Objects.reverse(AES_KEY_ENCODE);


    /* key算法 12位id | 42位时间戳 | 9位自增 */
    public static Collection<String> cdKey(long cdKeyId, int num) {
        return cdKey(AES_KEY_ENCODE, cdKeyId, num);
    }

    public static Collection<String> cdKey(int[] encode, final long cdKeyId, int num) {
        final long randomMax = 281474976710655L;
        AssertUtil.isTrue(cdKeyId < 4095, "cdKeyId 最大 4095");
        HashSet<String> list = new HashSet<>();
        while (num > 0) {
            long random = Long.parseLong("1" + RandomStringUtils.secure().next(14, false, true));
            AssertUtil.isTrue(random < randomMax, "random 错误");
            long randomKey = random << 12 | cdKeyId;
            BigInteger bigInteger = new BigInteger(String.valueOf(randomKey));
            String string = bigInteger.toString(36);
            String upperCase = string.toUpperCase();
            upperCase = AesUtil.convert_ASE(upperCase, encode);
            if (list.add(upperCase)) {
                num--;
            }
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
        long d = 0B1111_1111_1111L;
        long cdKeyId = longValue & d;
        return (int) cdKeyId;
    }

}
