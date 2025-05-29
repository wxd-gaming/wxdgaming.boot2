package wxdgaming.game.server.module.data;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

    /** key:account, value:mapping */
    private final ConcurrentHashMap<String, ClientSessionMapping> accountMappingMap = new ConcurrentHashMap<>();

}
