package wxdgaming.game.chart.script.inner.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.inner.InnerForwardMessage;

/**
 * 请求转发消息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class InnerForwardMessageHandler {

    /** 请求转发消息 */
    @ProtoRequest(InnerForwardMessage.class)
    public void innerForwardMessage(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        InnerForwardMessage message = event.buildMessage();
        
    }

}