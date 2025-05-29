package wxdgaming.game.gateway.module.fitler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.ProtoFilter;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerTrigger;
import wxdgaming.game.gateway.module.data.DataCenterService;

/**
 * 网关消息拦截
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 15:19
 **/
@Slf4j
@Singleton
public class ProtoClientFilterImpl implements ProtoFilter {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory listenerFactory;


    @Inject
    public ProtoClientFilterImpl(DataCenterService dataCenterService, ProtoListenerFactory listenerFactory) {
        this.dataCenterService = dataCenterService;
        this.listenerFactory = listenerFactory;
    }


    @Override public boolean doFilter(ProtoListenerTrigger protoListenerTrigger) {
        SocketSession socketSession = protoListenerTrigger.getSocketSession();
        if (socketSession.getType() == SocketSession.Type.client) {
            /*表示对内的session, 比如游戏服发过来的消息*/
            return true;
        } else if (socketSession.getType() == SocketSession.Type.server) {
            /*表示对外的session*/
            return true;
        }
        return true;
    }

}
