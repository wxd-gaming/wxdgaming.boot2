package wxdgaming.game.test.api;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.IWebSocketStringListener;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-17 21:24
 **/
@Slf4j
@Singleton
public class WebSocketStringListener implements IWebSocketStringListener {

    @Override public void onMessage(SocketSession socketSession, String message) {
        log.debug("{} String Listener {}", socketSession, message);
    }

}
