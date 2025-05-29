package wxdgaming.game.gateway.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.game.gateway.bean.ServerMapping;
import wxdgaming.game.gateway.module.data.DataCenterService;
import wxdgaming.game.message.inner.ServiceType;
import wxdgaming.game.message.role.ReqLogin;

/**
 * 登录请求
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqLoginHandler {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory protoListenerFactory;

    @Inject
    public ReqLoginHandler(DataCenterService dataCenterService, ProtoListenerFactory protoListenerFactory) {
        this.dataCenterService = dataCenterService;
        this.protoListenerFactory = protoListenerFactory;
    }


    /** 登录请求 */
    @ProtoRequest
    public void reqLogin(SocketSession socketSession, ReqLogin req) {
        int sid = req.getSid();
        ServerMapping serverMapping = dataCenterService.getServiceMappings().get(ServiceType.GAME, sid);
        if (serverMapping == null) {
            log.error("sid:{} 不存在", sid);
            socketSession.close("异常消息");
            return;
        }
        socketSession.bindData("gameServerId", sid);
        serverMapping.forwardMessage(socketSession.getUid(), req.msgId(), req.encode());

    }

}