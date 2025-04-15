package code;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import wxdgaming.boot2.core.lang.bit.BitFlag;
import wxdgaming.game.test.bean.StatusConst;

/**
 * 状态测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-15 19:56
 **/
@Slf4j
public class StatusConstTest {

    @Test
    public void s1() {
        BitFlag status = new BitFlag();
        status.addFlags(StatusConst.Online);
        log.info("{}", status);
        status.addFlags(StatusConst.Offline);
        log.info("{}", status);
        status.addFlags(StatusConst.ChangeMap);
        log.info("{}", status);
        status.addFlags(StatusConst.JoinMap);
        log.info("{}", status);
    }

}
