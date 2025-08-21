package wxdgaming.game.gateway.module.fitler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.ClientProtoFilter;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerTrigger;
import wxdgaming.game.gateway.module.data.DataCenterService;

/**
 * 游戏服务传递到网关
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-28 15:19
 **/
@Slf4j
@Component
public class GameServer2GatewayProtoFilterImpl implements ClientProtoFilter {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory listenerFactory;


    public GameServer2GatewayProtoFilterImpl(DataCenterService dataCenterService, ProtoListenerFactory listenerFactory) {
        this.dataCenterService = dataCenterService;
        this.listenerFactory = listenerFactory;
    }


    @Override public boolean doFilter(ProtoListenerTrigger protoListenerTrigger) {
        SocketSession socketSession = protoListenerTrigger.getSocketSession();
        if (socketSession.getType() == SocketSession.Type.client) {
            /*表示对内的session, 比如游戏服发过来的消息*/
            return true;
        }
        return true;
    }

}
