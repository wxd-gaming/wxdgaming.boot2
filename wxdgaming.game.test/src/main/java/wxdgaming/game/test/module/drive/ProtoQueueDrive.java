package wxdgaming.game.test.module.drive;

import com.google.inject.Singleton;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.starter.net.pojo.ProtoFilter;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerTrigger;
import wxdgaming.game.test.bean.role.Player;

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
        if (StringUtils.isBlank(protoListenerTrigger.getQueueName())) {
            Player player = protoListenerTrigger.getSocketSession().bindData("player");
            if (player != null) {
                protoListenerTrigger.setQueueName("drive-" + (player.getUid() % BootConfig.getIns().logicConfig().getMaxSize()));
            }
        }
        return true;
    }

}
