package code;

import org.junit.Test;
import wxdgaming.game.bean.goods.BagChangeDTO4ItemCfg;
import wxdgaming.game.core.Reason;
import wxdgaming.game.core.ReasonArgs;

import java.util.List;

public class BagChangeDTO4ItemCfgTest {

    @Test
    public void t1() {

        BagChangeDTO4ItemCfg build = BagChangeDTO4ItemCfg.builder()
                .setItemCfgList(List.of())
                .setReasonArgs(ReasonArgs.of(Reason.CreateRole))
                .build();

        System.out.println(build);

    }

}
