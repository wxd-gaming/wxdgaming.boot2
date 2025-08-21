package wxdgaming.game.server.script.inner.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.game.message.inner.InnerForwardMessage;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.InnerForwardEvent;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.ClientSessionService;
import wxdgaming.game.server.module.data.DataCenterService;

import java.util.List;

/**
 * 请求转发消息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class InnerForwardMessageHandler extends HoldApplicationContext {

    private final ClientSessionService clientSessionService;
    private final ProtoListenerFactory protoListenerFactory;
    private final DataCenterService dataCenterService;

    public InnerForwardMessageHandler(ClientSessionService clientSessionService,
                                      ProtoListenerFactory protoListenerFactory,
                                      DataCenterService dataCenterService) {
        this.clientSessionService = clientSessionService;
        this.protoListenerFactory = protoListenerFactory;
        this.dataCenterService = dataCenterService;
    }

    /** 请求转发消息 */
    @ProtoRequest(InnerForwardMessage.class)
    public void innerForwardMessage(ProtoEvent protoEvent) {
        SocketSession socketSession = protoEvent.getSocketSession();
        InnerForwardMessage req = protoEvent.buildMessage();
        List<Long> sessionIds = req.getSessionIds();
        int messageId = req.getMessageId();
        byte[] messages = req.getMessages();

        InnerForwardEvent.InnerForwardEventBuilder eventBuilder = InnerForwardEvent.builder()
                .applicationContextProvider(getApplicationContextProvider())
                .protoMapping(protoListenerFactory.getProtoListenerContent().getMappingMap().get(messageId))
                .socketSession(socketSession)
                .forwardMessage(req)
                .messageId(messageId)
                .bytes(messages);

        String clientIp = req.getKvBeansMap().get("clientIp");
        eventBuilder.clientIp(clientIp);
        String clientSessionId = req.getKvBeansMap().get("clientSessionId");
        eventBuilder.clientSessionId(Long.parseLong(clientSessionId));
        String account = req.getKvBeansMap().get("account");
        if (account != null) {
            ClientSessionMapping clientSessionMapping = clientSessionService.getAccountMappingMap().get(account);
            eventBuilder.clientSessionMapping(clientSessionMapping);
            long rid = clientSessionMapping.getRid();
            Player player = dataCenterService.getPlayer(rid);
            eventBuilder.player(player);
        }
        InnerForwardEvent forwardEvent = eventBuilder.build();

        protoListenerFactory.dispatch(socketSession, forwardEvent, null);
    }

}