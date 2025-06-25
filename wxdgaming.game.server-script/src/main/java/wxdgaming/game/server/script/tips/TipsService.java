package wxdgaming.game.server.script.tips;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.game.core.Reason;
import wxdgaming.game.message.tips.ResTips;
import wxdgaming.game.message.tips.TipsType;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.inner.InnerService;

import java.util.List;

/**
 * 提示
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 11:02
 **/
@Slf4j
@Singleton
public class TipsService {

    final InnerService innerService;
    final ProtoListenerFactory probeListenerFactory;

    @Inject
    public TipsService(InnerService innerService, ProtoListenerFactory probeListenerFactory) {
        this.innerService = innerService;
        this.probeListenerFactory = probeListenerFactory;
    }

    public void tips(Player player, String tips) {
        this.tips(player, tips, Reason.SYSTEM);
    }

    public void tips(Player player, String tips, Reason reason) {
        ClientSessionMapping clientSessionMapping = player.getClientSessionMapping();
        tips(clientSessionMapping.getSession(), clientSessionMapping.getClientSessionId(), TipsType.TIP_TYPE_NONE, tips, null, null, reason);
    }

    public void tips(ClientSessionMapping clientSessionMapping, String tips) {
        tips(clientSessionMapping.getSession(), clientSessionMapping.getClientSessionId(), tips);
    }

    public void tips(SocketSession socketSession, long clientSession, String tips) {
        tips(socketSession, clientSession, TipsType.TIP_TYPE_NONE, tips, null, null, Reason.SYSTEM);
    }

    public void tips(SocketSession socketSession, long clientSession, String tips, List<String> params) {
        tips(socketSession, clientSession, TipsType.TIP_TYPE_NONE, tips, params, null, Reason.SYSTEM);
    }

    public void tips(SocketSession socketSession, long clientSession, String tips, List<String> params, Class<? extends PojoBase> responseClass) {
        tips(socketSession, clientSession, TipsType.TIP_TYPE_NONE, tips, params, responseClass, Reason.SYSTEM);
    }

    public void tips(SocketSession socketSession, long clientSession, TipsType tipsType,
                     String tips, List<String> params,
                     Class<? extends PojoBase> responseClass,
                     Reason reason) {
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
        innerService.forwardMessage(socketSession, clientSession, resTips, null);
    }

}
