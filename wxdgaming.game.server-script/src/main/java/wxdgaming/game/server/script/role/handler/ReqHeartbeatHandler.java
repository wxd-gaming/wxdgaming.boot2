package wxdgaming.game.server.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.role.ReqHeartbeat;
import wxdgaming.game.message.role.ResHeartbeat;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;

/**
 * 心跳包
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqHeartbeatHandler {

    final DataCenterService dataCenterService;

    public ReqHeartbeatHandler(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }

    /** 心跳包 */
    @ProtoRequest(ReqHeartbeat.class)
    public void reqHeartbeat(ProtoEvent event) {
        UserMapping userMapping = event.bindData();
        ResHeartbeat resHeartbeat = new ResHeartbeat();
        resHeartbeat.setTimestamp(MyClock.millis());
        Player player = userMapping.player();
        player.write(resHeartbeat);
    }

}