package wxdgaming.game.robot.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.giftcode.ReqUseGiftCode;
import wxdgaming.game.message.role.ResChooseRole;
import wxdgaming.game.robot.bean.Robot;

/**
 * 选择角色响应
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResChooseRoleHandler {

    /** 选择角色响应 */
    @ProtoRequest(ResChooseRole.class)
    public void resChooseRole(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ResChooseRole req = event.buildMessage();
        Robot robot = socketSession.bindData("robot");
        robot.setLoginEnd(true);

        ReqUseGiftCode reqUseGiftCode = new ReqUseGiftCode();
        reqUseGiftCode.setGiftCode("vip666");
        socketSession.write(reqUseGiftCode);
    }

}