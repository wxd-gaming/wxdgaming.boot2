package wxdgaming.game.server.module.drive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.executor.ExecutorProperties;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerTrigger;
import wxdgaming.boot2.starter.net.pojo.ServerProtoFilter;
import wxdgaming.game.server.bean.MapKey;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;

/**
 * 消息队列
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 10:34
 **/
@Slf4j
@Component
public class ServerProtoQueueDrive implements ServerProtoFilter {


    final ExecutorProperties executorProperties;
    final DataCenterService dataCenterService;

    public ServerProtoQueueDrive(ExecutorProperties executorProperties, DataCenterService dataCenterService) {
        this.executorProperties = executorProperties;
        this.dataCenterService = dataCenterService;
    }

    @Override
    public boolean doFilter(ProtoListenerTrigger protoListenerTrigger) {
        UserMapping userMapping = protoListenerTrigger.getProtoEvent().getSocketSession().bindData("userMapping");
        protoListenerTrigger.getProtoEvent().bindData(userMapping);
        if (userMapping != null && userMapping.getRid() > 0) {
            Player player = dataCenterService.getPlayer(userMapping.getRid());
            if (StringUtils.isBlank(protoListenerTrigger.queueName())) {
                protoListenerTrigger.setQueueName("player-drive-" + (player.getUid() % executorProperties.getLogic().getCoreSize()));
            } else if ("map-drive".equalsIgnoreCase(protoListenerTrigger.queueName())) {
                MapKey mapKey = player.getMapKey();

            }
        }
        return true;
    }

}
