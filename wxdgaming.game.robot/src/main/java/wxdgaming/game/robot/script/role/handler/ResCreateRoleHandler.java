package wxdgaming.game.robot.script.role.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.role.ResCreateRole;

/**
 * 创建角色响应
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResCreateRoleHandler {

    /** 创建角色响应 */
    @ProtoRequest
    public void resCreateRole(SocketSession socketSession, ResCreateRole req) {

    }

}