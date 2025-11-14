package wxdgaming.game.server.module.drive;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.executor.ExecutorProperties;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerTrigger;
import wxdgaming.boot2.starter.net.pojo.ServerProtoFilter;
import wxdgaming.game.server.bean.UserMapping;
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
        if (userMapping != null) {
            if (StringUtils.isBlank(protoListenerTrigger.getQueueName())) {
                protoListenerTrigger.setQueueName("mapNpc-drive-" + (userMapping.getUserHashCode() % executorProperties.getLogic().getCoreSize()));
            } else if ("map-drive".equalsIgnoreCase(protoListenerTrigger.getQueueName())) {

            }
        }
        return true;
    }

}
