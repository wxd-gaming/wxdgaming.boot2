package wxdgaming.game.server.script.inner.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.game.message.inner.InnerRegisterServer;
import wxdgaming.game.message.inner.ServiceType;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.module.data.ClientSessionService;

import java.util.Collection;
import java.util.Objects;

/**
 * 注册服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Singleton
public class InnerRegisterServerHandler {

    private final GameServerProperties gameServerProperties;
    private final ProtoListenerFactory protoListenerFactory;
    private final ClientSessionService clientSessionService;

    @Inject
    public InnerRegisterServerHandler(GameServerProperties gameServerProperties, ProtoListenerFactory protoListenerFactory, ClientSessionService clientSessionService) {
        this.gameServerProperties = gameServerProperties;
        this.protoListenerFactory = protoListenerFactory;
        this.clientSessionService = clientSessionService;
    }

    /** 注册服务 */
    @ProtoRequest
    public void innerRegisterServer(SocketSession socketSession, InnerRegisterServer req) {
        ServiceType serviceType = req.getServiceType();
        /*网关过来，告诉游戏服务器，我是网关*/
        if (serviceType == ServiceType.GATEWAY) {
            int mainSid = req.getMainSid();
            SocketSession oldSession = clientSessionService.getServiceSocketSessionMapping().put(serviceType, mainSid, socketSession);
            if (oldSession != null && !Objects.equals(oldSession, socketSession)) {
                /* TODO 网关重连 ??? */
            }

            socketSession.bindData("serviceType", serviceType);
            socketSession.bindData("serviceId", mainSid);

            /*反向注册，告诉网关我是游戏服，并且告诉网关的基本信息*/
            InnerRegisterServer registerServer = new InnerRegisterServer();
            registerServer.setServiceType(ServiceType.GAME);
            registerServer.setGameId(gameServerProperties.getGid());
            registerServer.setMainSid(gameServerProperties.getSid());
            /*当前进程监听的服务器有哪些*/
            registerServer.getServerIds().add(gameServerProperties.getSid());
            /*当前监听的消息id集合*/
            Collection<Integer> values = protoListenerFactory.getProtoListenerContent().getMessage2MappingMap().values();
            registerServer.getMessageIds().addAll(values);
            if (log.isDebugEnabled()) {
                log.debug("游戏服主动发起网关注册基本信息{}", registerServer);
            }
            socketSession.write(registerServer);
        }
    }

}