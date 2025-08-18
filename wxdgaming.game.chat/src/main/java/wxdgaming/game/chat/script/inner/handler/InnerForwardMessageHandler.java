package wxdgaming.game.chat.script.inner.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.inner.InnerForwardMessage;

/**
 * 请求转发消息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Singleton
public class InnerForwardMessageHandler {

    /** 请求转发消息 */
    @ProtoRequest
    public void innerForwardMessage(SocketSession socketSession, InnerForwardMessage req) {
        
    }

}