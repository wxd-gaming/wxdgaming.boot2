package wxdgaming.game.robot.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.role.ResUpdateExp;
import wxdgaming.game.robot.bean.Robot;

/**
 * 更新经验
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUpdateExpHandler {

    /** 更新经验 */
    @ProtoRequest(ResUpdateExp.class)
    public void resUpdateExp(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ResUpdateExp req = event.buildMessage();
        Robot robot = socketSession.bindData("robot");
        robot.setExp(req.getExp());
        log.info("{} 更新经验:{}", robot, req);
    }

}