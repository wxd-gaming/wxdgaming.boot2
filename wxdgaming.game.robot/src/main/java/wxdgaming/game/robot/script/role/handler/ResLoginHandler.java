package wxdgaming.game.robot.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.role.ReqChooseRole;
import wxdgaming.game.message.role.ReqCreateRole;
import wxdgaming.game.message.role.ResLogin;
import wxdgaming.game.message.role.RoleBean;
import wxdgaming.game.robot.bean.Robot;

import java.util.List;

/**
 * 登录响应
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResLoginHandler {

    /** 登录响应 */
    @ProtoRequest(ResLogin.class)
    public void resLogin(ProtoEvent event) {
        ResLogin req = event.buildMessage();
        SocketSession socketSession = event.getSocketSession();
        Robot robot = socketSession.bindData("robot");
        log.info("登录响应:{}", req);
        List<RoleBean> roles = req.getRoles();
        if (roles.isEmpty()) {
            log.info("没有角色--开始创建角色");

            ReqCreateRole reqCreateRole = new ReqCreateRole();
            reqCreateRole.setName(RandomStringUtils.random(12, true, true));
            reqCreateRole.setSex(1);
            reqCreateRole.setJob(1);
            socketSession.write(reqCreateRole);
        } else {
            RoleBean first = roles.getFirst();
            robot.setName(first.getName());
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