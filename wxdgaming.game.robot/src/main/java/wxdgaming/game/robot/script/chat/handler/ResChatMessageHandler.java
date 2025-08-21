package wxdgaming.game.robot.script.chat.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.chat.ResChatMessage;
import wxdgaming.game.robot.bean.Robot;

/**
 * 聊天响应
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResChatMessageHandler {

    /** 聊天响应 */
    @ProtoRequest(ResChatMessage.class)
    public void resChatMessage(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ResChatMessage req = event.buildMessage();
        Robot robot = socketSession.bindData("robot");
        log.info("{} 收到聊天响应：{}", robot, req);
    }

}