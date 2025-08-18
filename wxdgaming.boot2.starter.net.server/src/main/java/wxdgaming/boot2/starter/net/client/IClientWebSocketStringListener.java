package wxdgaming.boot2.starter.net.client;

import wxdgaming.boot2.starter.net.SocketSession;

/**
 * 实现监听
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 12:14
 **/
public interface IClientWebSocketStringListener {

    void onMessage(SocketSession socketSession, String message);

}
