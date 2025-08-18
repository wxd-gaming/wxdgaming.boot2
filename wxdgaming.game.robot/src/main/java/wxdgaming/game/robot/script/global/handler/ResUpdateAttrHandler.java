package wxdgaming.game.robot.script.global.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.global.ResUpdateAttr;

/**
 * 更新属性
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Singleton
public class ResUpdateAttrHandler {

    /** 更新属性 */
    @ProtoRequest
    public void resUpdateAttr(SocketSession socketSession, ResUpdateAttr req) {

    }

}