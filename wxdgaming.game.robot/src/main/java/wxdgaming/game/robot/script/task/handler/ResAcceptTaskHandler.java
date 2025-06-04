package wxdgaming.game.robot.script.task.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.task.ResAcceptTask;
import wxdgaming.game.robot.bean.Robot;
import wxdgaming.game.server.cfg.QTaskTable;

/**
 * 接受任务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResAcceptTaskHandler {

    /** 接受任务 */
    @ProtoRequest
    public void resAcceptTask(SocketSession socketSession, ResAcceptTask req) {
        Robot robot = socketSession.bindData("robot");

        QTaskTable taskTable = DataRepository.getIns().dataTable(QTaskTable.class);
        log.info("{} 接取任务: {}", robot, taskTable.get(req.getTaskId()).getInnerTaskDetail());

        robot.getTasks().put(req.getTaskId(), req.getTask());
    }

}