package wxdgaming.game.test.script.role.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.bean.Robot;
import wxdgaming.game.test.script.role.message.ResChooseRole;

/**
 * 选择角色响应
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResChooseRoleHandler {

    /** 选择角色响应 */
    @ProtoRequest
    public void resChooseRole(SocketSession socketSession, ResChooseRole req) {
        Robot robot = socketSession.attribute("robot");
        robot.setLoginEnd(true);
    }

}