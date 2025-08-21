package wxdgaming.game.server.script.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.task.ReqTaskList;
import wxdgaming.game.server.bean.InnerForwardEvent;

/**
 * 任务列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqTaskListHandler {

    /** 任务列表 */
    @ProtoRequest(ReqTaskList.class)
    public void reqTaskList(InnerForwardEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ReqTaskList message = event.buildMessage();
        
    }

}