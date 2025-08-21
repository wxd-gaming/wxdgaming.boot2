package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.core.util.CDKeyUtil;

import java.math.BigInteger;
import java.util.Collection;

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
        Collection<String> strings = CDKeyUtil.cdKey(1001, 10);
        System.out.println(strings);
        String upperCase = strings.iterator().next();
        System.out.println(upperCase + " - " + upperCase.length());
        int cdKeyId = CDKeyUtil.getCdKeyId(upperCase);
        System.out.println(cdKeyId);
        AssertUtil.assertTrue(cdKeyId == 1001, "cdKeyId 错误");
    }

    @Test
    public void test2() throws Exception {
        Collection<String> strings = CDKeyUtil.cdKey(1, 100);
        System.out.println(strings);
        String string = strings.iterator().next();
        System.out.println(string + " - " + string.length());
        int cdKeyId = CDKeyUtil.getCdKeyId(string.toUpperCase());
        System.out.println(cdKeyId);
    }

}
