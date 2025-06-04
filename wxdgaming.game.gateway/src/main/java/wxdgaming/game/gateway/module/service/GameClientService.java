package wxdgaming.game.gateway.module.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.client.SocketClientConfig;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.server.http.HttpListenerFactory;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.message.inner.InnerRegisterServer;
import wxdgaming.game.message.inner.ServiceType;

/**
 * 网关服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 13:45
 **/
@Slf4j
@Getter
@Singleton
public class GameClientService extends HoldRunApplication {

    final ConcurrentTable<String, Integer, Gateway2GameSocketClientImpl> sessions = new ConcurrentTable<>();

    private final ProtoListenerFactory protoListenerFactory;
    private final HttpListenerFactory httpListenerFactory;

    @Inject
    public GameClientService(ProtoListenerFactory protoListenerFactory, HttpListenerFactory httpListenerFactory) {
        this.protoListenerFactory = protoListenerFactory;
        this.httpListenerFactory = httpListenerFactory;
    }

    @Start
    public void start() {
        checkGatewaySession();
    }

    @Scheduled("*/10")
    public void checkGatewaySession() {

        InnerRegisterServer registerServer = new InnerRegisterServer();
        registerServer.setServiceType(ServiceType.GATEWAY);
        log.info("{}", registerServer);

        checkGatewaySession("127.0.0.1", 8000, registerServer);
    }

    /** 网关主动连游戏服 */
    public void checkGatewaySession(final String inetHost, final int inetPort, InnerRegisterServer registerServer) {
        Gateway2GameSocketClientImpl gatewaySocketClient = getSessions().computeIfAbsent(inetHost, inetPort, l -> {
            SocketClientConfig socketClientConfig = BootConfig.getIns().getNestedValue("socket.client-forward", SocketClientConfig.class);
            socketClientConfig = (SocketClientConfig) socketClientConfig.clone();
            socketClientConfig.setHost(inetHost);
            socketClientConfig.setPort(inetPort);
            socketClientConfig.setMaxConnectionCount(1);
            socketClientConfig.setEnabledReconnection(false);
            Gateway2GameSocketClientImpl socketClient = new Gateway2GameSocketClientImpl(socketClientConfig);
            socketClient.init(protoListenerFactory, httpListenerFactory);
            return socketClient;
        });

        gatewaySocketClient.check(null);

        SocketSession socketSession = gatewaySocketClient.idle();
        if (socketSession != null) {
            if (socketSession.isOpen()) {
                socketSession.write(registerServer);
            }
        }
    }


}
