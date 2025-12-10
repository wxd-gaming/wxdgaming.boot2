package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.lang.ComboKey;

public class KeyTest {


    @Test
    public void test() {
        ComboKey comboKey1 = new ComboKey(1, 2);
        ComboKey comboKey2 = new ComboKey(1, 2);

        System.out.println(comboKey1 == comboKey2);
        System.out.println(comboKey1.equals(comboKey2));
        System.out.println(new ComboKey("1", "2").equals(new ComboKey("1", "3")));
        System.out.println(new ComboKey(1, 2).equals(new ComboKey(1, 2)));
        System.out.println(new ComboKey(1, "2").equals(new ComboKey(1, "2")));
    }

}
