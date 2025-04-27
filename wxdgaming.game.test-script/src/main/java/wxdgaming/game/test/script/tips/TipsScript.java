package wxdgaming.game.test.script.tips;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;

/**
 * 提示
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 11:02
 **/
@Slf4j
@Singleton
public class TipsScript {

    public void tips(SocketSession socketSession, String tips) {
        log.info("提示: {}", tips);
    }

}
