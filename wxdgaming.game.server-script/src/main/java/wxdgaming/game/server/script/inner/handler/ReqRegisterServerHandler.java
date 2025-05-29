package wxdgaming.game.server.script.inner.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.game.message.inner.ReqRegisterServer;
import wxdgaming.game.message.inner.ServiceType;

import java.util.Collection;

/**
 * null
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqRegisterServerHandler {

    private final ProtoListenerFactory protoListenerFactory;

    @Inject
    public ReqRegisterServerHandler(ProtoListenerFactory protoListenerFactory) {
        this.protoListenerFactory = protoListenerFactory;
    }

    /** null */
    @ProtoRequest
    public void reqRegisterServer(SocketSession socketSession, ReqRegisterServer req) {
        ServiceType serviceType = req.getServiceType();
        /*网关过来，告诉游戏服务器，我是网关*/
        if (serviceType == ServiceType.GATEWAY) {
            /*反向注册，告诉网关我是游戏服，并且告诉网关的基本信息*/
            ReqRegisterServer registerServer = new ReqRegisterServer();
            registerServer.setServiceType(ServiceType.GAME);
            registerServer.setGameId(BootConfig.getIns().gid());
            registerServer.setMainSid(BootConfig.getIns().sid());
            /*当前进程监听的服务器有哪些*/
            registerServer.getServerIds().add(BootConfig.getIns().sid());
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