package wxdgaming.game.robot.script.bag.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.bag.ResBagInfo;
import wxdgaming.game.robot.bean.Robot;

/**
 * 响应背包信息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResBagInfoHandler {

    /** 响应背包信息 */
    @ProtoRequest
    public void resBagInfo(SocketSession socketSession, ResBagInfo req) {
        Robot robot = socketSession.bindData("robot");
        log.info("{} 背包响应：\n{}", robot, req.toJSONString());
        robot.setItems(req.getItems());
        robot.setCurrencyMap(req.getCurrencyMap());
    }

}