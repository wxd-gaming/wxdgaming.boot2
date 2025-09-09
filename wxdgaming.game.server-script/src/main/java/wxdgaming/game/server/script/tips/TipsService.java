package wxdgaming.game.server.script.tips;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.message.tips.ResTips;
import wxdgaming.game.message.tips.TipsType;
import wxdgaming.game.server.bean.role.Player;

import java.util.List;

/**
 * 提示
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-27 11:02
 **/
@Slf4j
@Service
public class TipsService {

    final ProtoListenerFactory probeListenerFactory;

    public TipsService(ProtoListenerFactory probeListenerFactory) {
        this.probeListenerFactory = probeListenerFactory;
    }

    public void tips(Player player, String tips) {
        this.tips(player, tips, ReasonConst.SYSTEM);
    }

    public void tips(Player player, String tips, ReasonConst reason) {
        tips(player.getUserMapping().getSocketSession(), TipsType.TIP_TYPE_NONE, tips, null, null, reason);
    }

    public void tips(SocketSession socketSession, String tips) {
        tips(socketSession, TipsType.TIP_TYPE_NONE, tips, null, null, ReasonConst.SYSTEM);
    }

    public void tips(SocketSession socketSession, String tips, List<String> params) {
        tips(socketSession, TipsType.TIP_TYPE_NONE, tips, params, null, ReasonConst.SYSTEM);
    }

    public void tips(SocketSession socketSession, String tips, List<String> params, Class<? extends PojoBase> responseClass) {
        tips(socketSession, TipsType.TIP_TYPE_NONE, tips, params, responseClass, ReasonConst.SYSTEM);
    }

    public void tips(SocketSession socketSession, TipsType tipsType,
                     String tips, List<String> params,
                     Class<? extends PojoBase> responseClass,
                     ReasonConst reason) {
        log.info("提示: {}", tips);
        if (socketSession == null) {
            return;
        }
        ResTips resTips = new ResTips();
        resTips.setType(tipsType);
        resTips.setContent(tips);
        if (params != null) {
            resTips.getParams().addAll(params);
        }
        if (responseClass != null) {
            int messageId = probeListenerFactory.messageId(responseClass);
            resTips.setResMessageId(messageId);
        }
        if (reason != null) {
            resTips.setReason(reason.name());
        }
        socketSession.write(resTips);
    }

}
