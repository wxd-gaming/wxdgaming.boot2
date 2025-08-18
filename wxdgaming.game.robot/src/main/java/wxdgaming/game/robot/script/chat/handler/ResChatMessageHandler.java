package wxdgaming.game.robot.script.chat.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.chat.ResChatMessage;
import wxdgaming.game.robot.bean.Robot;

/**
 * 聊天响应
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Singleton
public class ResChatMessageHandler {

    /** 聊天响应 */
    @ProtoRequest
    public void resChatMessage(SocketSession socketSession, ResChatMessage req) {
        Robot robot = socketSession.bindData("robot");
        log.info("{} 收到聊天响应：{}", robot, req);
    }

}