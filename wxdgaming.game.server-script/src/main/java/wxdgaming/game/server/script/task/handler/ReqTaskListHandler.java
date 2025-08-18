package wxdgaming.game.server.script.task.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.task.ReqTaskList;
import wxdgaming.game.server.bean.role.Player;

/**
 * 任务列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Singleton
public class ReqTaskListHandler {

    /** 任务列表 */
    @ProtoRequest
    public void reqTaskList(SocketSession socketSession, ReqTaskList req, @ThreadParam(path = "player") Player player) {
        
    }

}