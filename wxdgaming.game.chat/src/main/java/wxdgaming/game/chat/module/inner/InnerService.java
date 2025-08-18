package wxdgaming.game.chat.module.inner;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.message.inner.ServiceType;

/**
 * 内嵌服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-09 14:18
 **/
@Slf4j
@Singleton
public class InnerService {

    final ConcurrentTable<ServiceType, Integer, SocketSession> sessionTable = new ConcurrentTable<>();

}
