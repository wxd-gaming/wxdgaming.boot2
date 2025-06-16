package wxdgaming.game.server.module.data;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.message.inner.ServiceType;
import wxdgaming.game.server.bean.ClientSessionMapping;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ClientSession
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 20:42
 **/
@Slf4j
@Getter
@Singleton
public class ClientSessionService {

    /** 服务映射,R:服务类型, C:服务id（网关id）, V:session */
    private final ConcurrentTable<ServiceType, Integer, SocketSession> serviceSocketSessionMapping = new ConcurrentTable<>();

    /** key:account, value:mapping */
    private final ConcurrentHashMap<String, ClientSessionMapping> accountMappingMap = new ConcurrentHashMap<>();

    public ClientSessionMapping getMapping(String account) {
        return accountMappingMap.computeIfAbsent(account, l -> new ClientSessionMapping().setAccount(account));
    }

}
