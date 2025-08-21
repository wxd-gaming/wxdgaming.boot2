package wxdgaming.game.server.script.bag.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.bag.ReqBagInfo;
import wxdgaming.game.server.bean.InnerForwardEvent;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.BagService;

/**
 * 请求背包信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqBagInfoHandler {

    final BagService bagService;

    public ReqBagInfoHandler(BagService bagService) {
        this.bagService = bagService;
    }

    /** 请求背包信息 */
    @ProtoRequest(ReqBagInfo.class)
    public void reqBagInfo(InnerForwardEvent event) {
        Player player = event.getPlayer();
        ReqBagInfo req = event.buildMessage();
        bagService.sendBagInfo(player, req.getBagType());
    }

}