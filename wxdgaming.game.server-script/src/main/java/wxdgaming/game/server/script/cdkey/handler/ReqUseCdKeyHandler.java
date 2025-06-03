package wxdgaming.game.server.script.cdkey.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.cdkey.ReqUseCdKey;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.cdkey.CDKeyService;

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
    @ExecutorWith(queueName = "use-cdKey")
    public void reqUseCdKey(SocketSession socketSession, ReqUseCdKey req,
                            @ThreadParam(path = "player") Player player) {
        cdKeyService.use(player, req.getCdKey());
    }

}