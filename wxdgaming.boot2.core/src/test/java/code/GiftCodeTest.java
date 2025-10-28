package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.core.util.GiftCodeUtil;

import java.math.BigInteger;
import java.util.Collection;

public class GiftCodeTest {

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
        Collection<String> strings = GiftCodeUtil.giftCode(1001, 10);
        System.out.println(strings);
        String upperCase = strings.iterator().next();
        System.out.println(upperCase + " - " + upperCase.length());
        int giftCodeId = GiftCodeUtil.getGiftCodeId(upperCase);
        System.out.println(giftCodeId);
        AssertUtil.isTrue(giftCodeId == 1001, "giftCodeId 错误");
    }

    @Test
    public void test2() throws Exception {
        Collection<String> strings = GiftCodeUtil.giftCode(1, 100);
        System.out.println(strings);
        String string = strings.iterator().next();
        System.out.println(string + " - " + string.length());
        int giftCodeId = GiftCodeUtil.getGiftCodeId(string.toUpperCase());
        System.out.println(giftCodeId);
    }

}
