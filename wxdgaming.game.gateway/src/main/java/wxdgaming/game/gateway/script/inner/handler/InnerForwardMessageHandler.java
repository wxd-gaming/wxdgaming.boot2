package wxdgaming.game.gateway.script.inner.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.starter.net.MessageEncode;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.pojo.ProtoMapping;
import wxdgaming.game.gateway.bean.UserMapping;
import wxdgaming.game.gateway.module.data.DataCenterService;
import wxdgaming.game.message.inner.InnerForwardMessage;

import java.util.List;

/**
 * 请求转发消息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class InnerForwardMessageHandler {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory protoListenerFactory;

    @Inject
    public InnerForwardMessageHandler(DataCenterService dataCenterService, ProtoListenerFactory protoListenerFactory) {
        this.dataCenterService = dataCenterService;
        this.protoListenerFactory = protoListenerFactory;
    }

    /** 请求转发消息 */
    @ProtoRequest
    public void reqForwardMessage(SocketSession socketSession, InnerForwardMessage req) {
        int messageId = req.getMessageId();
        byte[] messages = req.getMessages();
        ProtoMapping protoMapping = protoListenerFactory.getProtoListenerContent().getMappingMap().get(messageId);
        List<Integer> gameIds = req.getGameIds();
        List<Integer> serverIds = req.getServerIds();
        List<Long> sessionIds = req.getSessionIds();
        for (Long sessionId : sessionIds) {
            SocketSession clientSession = dataCenterService.getClientSession(sessionId);
            if (clientSession != null) {
                UserMapping userMapping = clientSession.bindData("userMapping");
                /*TODO重构队列，1是为了效率把消息分散队列，2是为了绝对的保证消息的顺序*/
                String queueName = "session-drive-" + clientSession.getUid() % 16;
                if (protoMapping != null) {
                    ThreadContext.putContent("forwardMessage", req);
                    ThreadContext.putContent("clientSession", clientSession);
                    ThreadContext.putContent("userMapping", userMapping);
                    protoListenerFactory.dispatch(socketSession, messageId, messages, () -> queueName);
                } else {
                    ReqForwardMessageRunnable command = new ReqForwardMessageRunnable(userMapping, messageId, messages);
                    command.setQueueName(queueName);
                    ExecutorFactory.getExecutorServiceLogic().execute(command);
                }
            }
        }
    }

    public static class ReqForwardMessageRunnable extends ExecutorEvent {

        final UserMapping userMapping;
        final int messageId;
        final byte[] bytes;

        public ReqForwardMessageRunnable(UserMapping userMapping, int messageId, byte[] bytes) {
            this.userMapping = userMapping;
            this.messageId = messageId;
            this.bytes = bytes;
        }

        @Override public String queueName() {
            return super.queueName();
        }

        @Override public void onEvent() throws Exception {
            if (log.isDebugEnabled()) {
                log.debug("请求转发消息到客户端:{}, msgId={}", userMapping, messageId);
            }
            userMapping.send2Client(messageId, bytes);
        }

    }

}