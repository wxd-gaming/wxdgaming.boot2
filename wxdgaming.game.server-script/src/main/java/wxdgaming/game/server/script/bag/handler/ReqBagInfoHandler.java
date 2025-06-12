package wxdgaming.game.server.script.bag.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.bag.ReqBagInfo;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.BagService;

/**
 * 请求背包信息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqBagInfoHandler {

    final BagService bagService;

    @Inject
    public ReqBagInfoHandler(BagService bagService) {
        this.bagService = bagService;
    }

    /** 请求背包信息 */
    @ProtoRequest
    public void reqBagInfo(SocketSession socketSession, ReqBagInfo req, @ThreadParam(path = "player") Player player) {
        bagService.sendBagInfo(player, req.getBagType());
    }

}