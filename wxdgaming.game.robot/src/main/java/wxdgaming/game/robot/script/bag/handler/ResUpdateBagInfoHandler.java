package wxdgaming.game.robot.script.bag.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.bag.ResUpdateBagInfo;
import wxdgaming.game.robot.bean.Robot;

/**
 * 响应背包信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUpdateBagInfoHandler {

    /** 响应背包信息 */
    @ProtoRequest(ResUpdateBagInfo.class)
    public void resUpdateBagInfo(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ResUpdateBagInfo req = event.buildMessage();
        Robot robot = socketSession.bindData("robot");
        log.info("{} 背包更新响应：\n{}", robot, req.toJSONString());
        robot.getItems().entrySet().removeIf(v -> req.getDelItemIds().contains(v.getKey()));
        robot.getItems().putAll(req.getChangeItems());
        robot.getCurrencyMap().putAll(req.getCurrencyMap());
    }

}