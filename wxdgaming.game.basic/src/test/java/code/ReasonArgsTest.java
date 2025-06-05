package code;

import org.junit.Test;
import wxdgaming.game.core.Reason;
import wxdgaming.game.core.ReasonArgs;

public class ReasonArgsTest {

    @Test
    public void t1() {
        ReasonArgs reasonArgs = ReasonArgs.of(Reason.GM, "aa", "bb");
        System.out.println(reasonArgs);
        System.out.println(reasonArgs.toJSONString());
    }

}
