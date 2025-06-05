package wxdgaming.game.robot.script.bag.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.bag.ResUpdateBagInfo;
import wxdgaming.game.robot.bean.Robot;

/**
 * 响应背包信息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResUpdateBagInfoHandler {

    /** 响应背包信息 */
    @ProtoRequest
    public void resUpdateBagInfo(SocketSession socketSession, ResUpdateBagInfo req) {
        Robot robot = socketSession.bindData("robot");
        log.info("{} 背包更新响应：\n{}", robot, req.toJSONStringAsFmt());
        robot.getItems().removeIf(v -> req.getItems().stream().anyMatch(r -> r.getUid() == v.getUid()));
        robot.getItems().addAll(req.getItems());
        robot.getCurrencyMap().putAll(req.getCurrencyMap());
    }

}