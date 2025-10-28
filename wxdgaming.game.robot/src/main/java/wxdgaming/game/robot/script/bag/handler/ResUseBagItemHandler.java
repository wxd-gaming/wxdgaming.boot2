package wxdgaming.game.robot.script.bag.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.bag.ResBagInfo;
import wxdgaming.game.message.bag.ResUseBagItem;
import wxdgaming.game.robot.bean.Robot;

/**
 * 回复使用道具
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUseBagItemHandler {

    /** 回复使用道具 */
    @ProtoRequest(ResUseBagItem.class)
    public void resUseBagItem(ProtoEvent event) {
        ResUseBagItem message = event.buildMessage();
        SocketSession socketSession = event.getSocketSession();
        Robot robot = socketSession.bindData("robot");

    }

}