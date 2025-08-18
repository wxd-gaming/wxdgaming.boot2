package wxdgaming.game.robot.script.role.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.role.ResUpdateFightValue;

/**
 * 更新战斗力
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Singleton
public class ResUpdateFightValueHandler {

    /** 更新战斗力 */
    @ProtoRequest
    public void resUpdateFightValue(SocketSession socketSession, ResUpdateFightValue req) {

    }

}