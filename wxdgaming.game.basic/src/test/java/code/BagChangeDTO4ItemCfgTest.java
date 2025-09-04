package code;

import org.junit.jupiter.api.Test;
import wxdgaming.game.bean.goods.BagChangeDTO4ItemCfg;
import wxdgaming.game.basic.core.Reason;
import wxdgaming.game.basic.core.ReasonDTO;

import java.util.Collections;
import java.util.List;

public class BagChangeDTO4ItemCfgTest {

    @Test
    public void t1() {

        BagChangeDTO4ItemCfg build = BagChangeDTO4ItemCfg.builder()
                .setItemCfgList(Collections.emptyList())
                .setReasonDTO(ReasonDTO.of(Reason.CreateRole))
                .build();

        System.out.println(build);

    }

}
