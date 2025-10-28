package wxdgaming.boot2.core.util;

import org.apache.commons.lang3.RandomStringUtils;
import wxdgaming.boot2.core.io.Objects;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;

public class GiftCodeUtil {

    private static final int[] AES_KEY_ENCODE = {1, 4, 3, 6, 2};
    private static final int[] AES_KEY_DECODE = Objects.reverse(AES_KEY_ENCODE);


    /* key算法 12位id | 42位时间戳 | 9位自增 */
    public static Collection<String> giftCode(long giftCodeId, int num) {
        return giftCode(AES_KEY_ENCODE, giftCodeId, num);
    }

    public static Collection<String> giftCode(int[] encode, final long giftCodeId, int num) {
        final long randomMax = 281474976710655L;
        AssertUtil.isTrue(giftCodeId < 4095, "GiftCodeId 最大 4095");
        HashSet<String> list = new HashSet<>();
        while (num > 0) {
            long random = Long.parseLong("1" + RandomStringUtils.secure().next(14, false, true));
            AssertUtil.isTrue(random < randomMax, "random 错误");
            long randomKey = random << 12 | giftCodeId;
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

    public static int getGiftCodeId(String giftCode) {
        return getGiftCodeId(AES_KEY_DECODE, giftCode);
    }

    public static int getGiftCodeId(int[] decode, String giftCode) {
        giftCode = AesUtil.convert_ASE(giftCode, decode);
        BigInteger bigInteger = new BigInteger(giftCode, 36);
        long longValue = bigInteger.longValue();
        long d = 0B1111_1111_1111L;
        long giftCodeId = longValue & d;
        return (int) giftCodeId;
    }

}
