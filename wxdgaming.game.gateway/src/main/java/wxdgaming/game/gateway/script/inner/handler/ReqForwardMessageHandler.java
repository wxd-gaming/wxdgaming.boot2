package wxdgaming.game.gateway.script.inner.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.starter.net.MessageEncode;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.pojo.ProtoMapping;
import wxdgaming.game.gateway.module.data.DataCenterService;
import wxdgaming.game.message.inner.ReqForwardMessage;

import java.util.List;

/**
 * 请求转发消息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqForwardMessageHandler {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory protoListenerFactory;

    @Inject
    public ReqForwardMessageHandler(DataCenterService dataCenterService, ProtoListenerFactory protoListenerFactory) {
        this.dataCenterService = dataCenterService;
        this.protoListenerFactory = protoListenerFactory;
    }

    /** 请求转发消息 */
    @ProtoRequest
    public void reqForwardMessage(SocketSession socketSession, ReqForwardMessage req) {
        int messageId = req.getMessageId();
        byte[] messages = req.getMessages();
        ProtoMapping protoMapping = protoListenerFactory.getProtoListenerContent().getMappingMap().get(messageId);
        if (protoMapping != null) {
            ThreadContext.putContent("forwardMessage", req);
            protoListenerFactory.dispatch(socketSession, messageId, messages);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("请求转发消息:{}", req);
            }
            List<Integer> gameIds = req.getGameIds();
            List<Integer> serverIds = req.getServerIds();
            List<Long> sessionIds = req.getSessionIds();
            for (Long sessionId : sessionIds) {
                SocketSession session = dataCenterService.getClientSession(sessionId);
                if (session != null) {
                    Object build = MessageEncode.build(session, messageId, messages);
                    session.write(build);
                }
            }
        }
    }

}