package code;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-04 13:17
 **/
public class RandomUtilTest {

    @Test
    public void r1(){
        System.out.println(RandomStringUtils.secure().next(50,true,true));
    }

    @RepeatedTest(10)
    public void randomNumber(){
        System.out.println(RandomStringUtils.secure().next(14,false,true));
    }

}
