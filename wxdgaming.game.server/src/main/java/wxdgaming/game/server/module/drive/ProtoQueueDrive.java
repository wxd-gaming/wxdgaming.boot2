package wxdgaming.game.server.module.drive;

import com.google.inject.Singleton;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.starter.net.pojo.ProtoFilter;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerTrigger;
import wxdgaming.game.server.bean.MapKey;
import wxdgaming.game.server.bean.role.Player;

/**
 * 消息队列
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 10:34
 **/
@Singleton
public class ProtoQueueDrive implements ProtoFilter {


    @Override
    public boolean doFilter(ProtoListenerTrigger protoListenerTrigger) {
        Player player = protoListenerTrigger.getSocketSession().bindData("player");
        if (player != null) {
            if (StringUtils.isBlank(protoListenerTrigger.queueName())) {
                protoListenerTrigger.setQueueName("drive-" + (player.getUid() % BootConfig.getIns().logicConfig().getCoreSize()));
            } else if ("map-drive".equalsIgnoreCase(protoListenerTrigger.queueName())) {
                MapKey mapKey = player.getMapKey();

            }
        }
        return true;
    }

}
