package code;

import org.junit.jupiter.api.Test;
import wxdgaming.game.basic.core.Reason;
import wxdgaming.game.basic.core.ReasonDTO;

public class ReasonDTOTest {

    @Test
    public void t1() {
        ReasonDTO reasonDTO = ReasonDTO.of(Reason.GM, "aa", "bb");
        System.out.println(reasonDTO);
        System.out.println(reasonDTO.toJSONString());
    }

}
