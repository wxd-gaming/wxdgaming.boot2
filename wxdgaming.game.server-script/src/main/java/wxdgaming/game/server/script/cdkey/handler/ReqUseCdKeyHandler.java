package wxdgaming.game.server.script.cdkey.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.cdkey.ReqUseCdKey;
import wxdgaming.game.server.bean.InnerForwardEvent;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.cdkey.CDKeyService;

/**
 * 请求使用cdkey
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqUseCdKeyHandler {

    private final CDKeyService cdKeyService;


    public ReqUseCdKeyHandler(CDKeyService cdKeyService) {
        this.cdKeyService = cdKeyService;
    }

    /** 请求使用cdkey */
    @ProtoRequest(ReqUseCdKey.class)
    @ExecutorWith(queueName = "use-cdKey")
    public void reqUseCdKey(InnerForwardEvent event) {
        Player player = event.getPlayer();
        ReqUseCdKey req = event.buildMessage();
        cdKeyService.use(player, req.getCdKey());
    }

}