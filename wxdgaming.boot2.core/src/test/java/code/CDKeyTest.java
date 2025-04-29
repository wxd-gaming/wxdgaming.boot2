package code;

import org.junit.Test;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.util.AesUtil;
import wxdgaming.boot2.core.util.AssertUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CDKeyTest {

    @Test
    public void test() {
        long id = 1001;
        id = id << 51;
        System.out.println(id);
        long second = System.currentTimeMillis();
        System.out.println(second);
        System.out.println(second << 9);
        BigInteger bigInteger = new BigInteger(String.valueOf(id));
        String string = bigInteger.toString(36);
        System.out.println(string.toUpperCase() + " - " + string.length());
    }

    @Test
    public void createKey() {
        List<String> strings =CdKey.cdKey(1001, 10);
        System.out.println(strings);
        String upperCase = strings.getFirst();
        System.out.println(upperCase + " - " + upperCase.length());
        int cdKeyId = CdKey.getCdKeyId(upperCase);
        System.out.println(cdKeyId);
        AssertUtil.assertTrue(cdKeyId == 1001, "cdKeyId 错误");
    }

    public class CdKey {

        private static final int[] AES_KEY_ENCODE = {1, 2, 5, 3, 2};
        private static final int[] AES_KEY_DECODE = Objects.reverse(AES_KEY_ENCODE);


        /* key算法 12位id | 42位时间戳 | 9位自增 */
        public static List<String> cdKey(long cdKeyId, int num) {
            AssertUtil.assertTrue(num <= 511, "cdKeyId 最大生成 511 个");
            AssertUtil.assertTrue(cdKeyId < 4095, "cdKeyId 最大 4095");
            cdKeyId = cdKeyId << 51;
            long second = System.currentTimeMillis() / 1000;
            cdKeyId = cdKeyId | second << 17;
            List<String> list = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                cdKeyId += 1;
                BigInteger bigInteger = new BigInteger(String.valueOf(cdKeyId));
                String string = bigInteger.toString(36);
                String upperCase = string.toUpperCase();
                upperCase = AesUtil.convert_ASE(upperCase, AES_KEY_ENCODE);
                list.add(upperCase);
            }
            return list;
        }

        public static int getCdKeyId(String cdKey) {
            cdKey = AesUtil.convert_ASE(cdKey, AES_KEY_DECODE);
            BigInteger bigInteger = new BigInteger(cdKey, 36);
            long longValue = bigInteger.longValue();
            long cdKeyId = longValue >> 51;
            return (int) cdKeyId;
        }

    }


}
