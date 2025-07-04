package wxdgaming.game.robot.script.role.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.role.ReqChooseRole;
import wxdgaming.game.message.role.ReqCreateRole;
import wxdgaming.game.message.role.ResLogin;
import wxdgaming.game.message.role.RoleBean;
import wxdgaming.game.robot.bean.Robot;

import java.util.List;

/**
 * 登录响应
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResLoginHandler {

    /** 登录响应 */
    @ProtoRequest
    public void resLogin(SocketSession socketSession, ResLogin req) {
        Robot robot = socketSession.bindData("robot");
        log.info("登录响应:{}", req);
        List<RoleBean> roles = req.getRoles();
        if (roles.isEmpty()) {
            log.info("没有角色--开始创建角色");

            ReqCreateRole reqCreateRole = new ReqCreateRole();
            reqCreateRole.setName(robot.getName());
            reqCreateRole.setSex(1);
            reqCreateRole.setJob(1);
            socketSession.write(reqCreateRole);
        } else {
            RoleBean first = roles.getFirst();
            robot.setRid(first.getRid());
            robot.setLevel(first.getLevel());
            robot.setExp(first.getExp());
            log.info("选择角色:{}", robot);
            ReqChooseRole reqChooseRole = new ReqChooseRole();
            reqChooseRole.setRid(first.getRid());
            socketSession.write(reqChooseRole);
        }
    }

}