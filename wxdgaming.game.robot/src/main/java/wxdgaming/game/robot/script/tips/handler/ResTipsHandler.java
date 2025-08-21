package wxdgaming.game.robot.script.tips.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.tips.ResTips;

/**
 * 提示内容
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResTipsHandler {

    /** 提示内容 */
    @ProtoRequest
    public void resTips(SocketSession socketSession, ResTips req) {

        log.info("收到提示内容:{} {}", req.getType(), req.getContent());

    }

}