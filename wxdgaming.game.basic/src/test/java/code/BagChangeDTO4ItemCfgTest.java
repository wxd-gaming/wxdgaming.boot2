package code;

import org.junit.jupiter.api.Test;
import wxdgaming.game.server.bean.goods.BagChangeDTO4ItemCfg;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.server.bean.reason.ReasonDTO;

import java.util.Collections;

public class BagChangeDTO4ItemCfgTest {

    @Test
    public void t1() {

        BagChangeDTO4ItemCfg build = BagChangeDTO4ItemCfg.builder()
                .setItemCfgList(Collections.emptyList())
                .setReasonDTO(ReasonDTO.of(ReasonConst.CreateRole))
                .build();

        System.out.println(build);

    }

}
