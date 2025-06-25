package code;

import org.junit.Test;
import wxdgaming.game.bean.goods.BagChangeArgs4ItemCfg;
import wxdgaming.game.core.Reason;
import wxdgaming.game.core.ReasonArgs;

import java.util.List;

public class BagChangeArgs4ItemCfgTest {

    @Test
    public void t1() {

        BagChangeArgs4ItemCfg build = BagChangeArgs4ItemCfg.builder()
                .setItemCfgList(List.of())
                .setReasonArgs(ReasonArgs.of(Reason.CreateRole))
                .build();

        System.out.println(build);

    }

}
