package code;

import org.junit.jupiter.api.Test;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.server.bean.reason.ReasonDTO;

public class ReasonDTOTest {

    @Test
    public void t1() {
        ReasonDTO reasonDTO = ReasonDTO.of(ReasonConst.GM, "aa", "bb");
        System.out.println(reasonDTO);
        System.out.println(reasonDTO.toJSONString());
    }

}
