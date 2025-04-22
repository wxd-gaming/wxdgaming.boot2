package wxdgaming.game.test.script.role.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.script.role.message.ReqChooseRole;

/**
 * 选择角色
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqChooseRoleHandler {

    /** 选择角色 */
    @ProtoRequest
    public void reqChooseRole(SocketSession socketSession, ReqChooseRole req) {

    }

}