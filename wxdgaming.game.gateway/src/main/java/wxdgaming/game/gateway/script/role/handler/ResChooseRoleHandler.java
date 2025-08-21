package wxdgaming.game.gateway.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.game.gateway.bean.InnerForwardEvent;
import wxdgaming.game.gateway.bean.UserMapping;
import wxdgaming.game.gateway.module.data.DataCenterService;
import wxdgaming.game.message.inner.InnerForwardMessage;
import wxdgaming.game.message.role.ResChooseRole;

import java.util.List;

/**
 * 选择角色响应
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResChooseRoleHandler {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory protoListenerFactory;

    public ResChooseRoleHandler(DataCenterService dataCenterService, ProtoListenerFactory protoListenerFactory) {
        this.dataCenterService = dataCenterService;
        this.protoListenerFactory = protoListenerFactory;
    }

    /** 选择角色响应 */
    @ProtoRequest(ResChooseRole.class)
    public void resChooseRole(InnerForwardEvent event) {
        InnerForwardMessage forwardMessage = event.getForwardMessage();
        ResChooseRole req = event.buildMessage();
        List<Long> sessionIds = forwardMessage.getSessionIds();
        Long sessionId = sessionIds.getFirst();
        SocketSession clientSession = dataCenterService.getClientSession(sessionId);
        String account = clientSession.bindData("account");
        long rid = req.getRid();
        UserMapping userMapping = dataCenterService.getUserMapping(account);
        userMapping.setChooseRoleId(rid);
        clientSession.write(req);
    }

}