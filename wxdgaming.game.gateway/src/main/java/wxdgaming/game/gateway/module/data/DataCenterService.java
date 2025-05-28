package wxdgaming.game.gateway.module.data;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.gateway.module.bean.ServerMapping;
import wxdgaming.game.message.inner.ReqRegisterServer;
import wxdgaming.game.message.inner.ServiceType;

import java.util.List;
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
    private final ConcurrentHashMap<Integer, ServerMapping> gameServiceMappings = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, SocketSession> clientSessions = new ConcurrentHashMap<>();

    public SocketSession registerClientSession(SocketSession socketSession) {
        return clientSessions.put(socketSession.getUid(), socketSession);
    }

    public void registerServerMapping(ReqRegisterServer reqRegisterServer) {
        ServiceType serviceType = reqRegisterServer.getServiceType();
        List<Integer> serverIds = reqRegisterServer.getServerIds();
        int mainSid = reqRegisterServer.getMainSid();
        ServerMapping serverMapping = gameServiceMappings.computeIfAbsent(mainSid, k -> new ServerMapping());

        serverMapping.setGid(mainSid);
        serverMapping.setMainSid(mainSid);
        serverMapping.setSid(serverIds);
        serverMapping.getMessageIds().addAll(reqRegisterServer.getMessageIds());
        for (Integer serverId : serverIds) {
            /*覆盖子服的映射*/
            gameServiceMappings.put(serverId, serverMapping);
            log.info("收到服务 {} sid={} 注册 {}", serviceType, serverId, serverMapping);
        }

    }

}
