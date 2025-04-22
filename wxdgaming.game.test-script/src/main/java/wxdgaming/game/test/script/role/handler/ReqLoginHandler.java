package wxdgaming.game.test.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlService;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.event.EventBus;
import wxdgaming.game.test.script.event.OnLogin;
import wxdgaming.game.test.script.event.OnLoginBefore;
import wxdgaming.game.test.script.role.message.ReqLogin;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqLoginHandler {

    private final EventBus eventBus;
    private final PgsqlService psqlService;

    @Inject
    public ReqLoginHandler(EventBus eventBus, PgsqlService psqlService) {
        this.eventBus = eventBus;
        this.psqlService = psqlService;
    }

    @ProtoRequest
    public void reqLogin(SocketSession socketSession, ReqLogin req) {
        long playerId = 1;
        Player player = psqlService.getCacheService().cache(Player.class, playerId);
        eventBus.post(OnLoginBefore.class, player);

        eventBus.post(OnLogin.class, player, 1, 1);

        eventBus.post(OnLogin.class, player);

    }

}