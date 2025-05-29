package wxdgaming.game.gateway.module.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.server.SocketServerImpl;
import wxdgaming.game.gateway.bean.ServerMapping;
import wxdgaming.game.gateway.bean.UserMapping;
import wxdgaming.game.message.inner.ReqRegisterServer;
import wxdgaming.game.message.inner.ServiceType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据中心
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 10:27
 **/
@Slf4j
@Getter
@Singleton
public class DataCenterService extends HoldRunApplication {

    /** 服务映射 */
    private final ConcurrentTable<ServiceType, Integer, ServerMapping> serviceMappings = new ConcurrentTable<>();
    /** key:account, value:mapping */
    private final ConcurrentHashMap<String, UserMapping> userMappings = new ConcurrentHashMap<>();
    private final SocketServerImpl socketServer;

    @Inject
    public DataCenterService(SocketServerImpl socketServer) {
        this.socketServer = socketServer;
    }

    public void registerServerMapping(SocketSession socketSession, ReqRegisterServer reqRegisterServer) {
        ServiceType serviceType = reqRegisterServer.getServiceType();

        Map<Integer, ServerMapping> serverMappingMap = serviceMappings.row(serviceType);

        List<Integer> serverIds = reqRegisterServer.getServerIds();
        int mainSid = reqRegisterServer.getMainSid();

        ServerMapping serverMapping = serverMappingMap.computeIfAbsent(mainSid, k -> new ServerMapping());
        serverMapping.setSession(socketSession);
        serverMapping.setGid(mainSid);
        serverMapping.setMainSid(mainSid);
        serverMapping.setSid(serverIds);
        serverMapping.getMessageIds().addAll(reqRegisterServer.getMessageIds());

        for (Integer serverId : serverIds) {
            /*覆盖子服的映射*/
            serverMappingMap.put(serverId, serverMapping);
            if (log.isDebugEnabled()) {
                log.debug("收到服务 {} sid={} 注册 {}", serviceType, serverId, serverMapping);
            }
        }
    }

    public UserMapping getUserMapping(String account) {
        return userMappings.computeIfAbsent(account, l -> new UserMapping().setAccount(account));
    }

    public SocketSession getClientSession(long sessionId) {
        return socketServer.getSessionGroup().getChannelMap().get(sessionId);
    }

}
