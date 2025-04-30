package wxdgaming.game.test.script.tips;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.game.test.script.tips.message.ResTips;
import wxdgaming.game.test.script.tips.message.TipsType;

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

    final ProtoListenerFactory probeListenerFactory;

    @Inject
    public TipsService(ProtoListenerFactory probeListenerFactory) {
        this.probeListenerFactory = probeListenerFactory;
    }


    public void tips(SocketSession socketSession, String tips) {
        tips(socketSession, TipsType.TIP_TYPE_NONE, tips, null, null);
    }

    public void tips(SocketSession socketSession, String tips, List<String> params) {
        tips(socketSession, TipsType.TIP_TYPE_NONE, tips, params, null);
    }

    public void tips(SocketSession socketSession, String tips, List<String> params, Class<? extends PojoBase> responseClass) {
        tips(socketSession, TipsType.TIP_TYPE_NONE, tips, params, responseClass);
    }

    public void tips(SocketSession socketSession, TipsType tipsType, String tips, List<String> params, Class<? extends PojoBase> responseClass) {
        log.info("提示: {}", tips);
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
        socketSession.write(resTips);
    }

}
