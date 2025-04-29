package wxdgaming.game.test.script.cdkey.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.cdkey.CDKeyService;
import wxdgaming.game.test.script.cdkey.message.ReqUseCdKey;

/**
 * 请求使用cdkey
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqUseCdKeyHandler {

    private final CDKeyService cdKeyService;


    @Inject
    public ReqUseCdKeyHandler(CDKeyService cdKeyService) {
        this.cdKeyService = cdKeyService;
    }

    /** 请求使用cdkey */
    @ProtoRequest
    public void reqUseCdKey(SocketSession socketSession, ReqUseCdKey req) {
        Player player = socketSession.attribute("player");
        cdKeyService.use(player, req.getCdKey());
    }

}