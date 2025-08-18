package wxdgaming.game.robot.script.task.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.task.ResUpdateTaskList;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.robot.bean.Robot;
import wxdgaming.game.cfg.QTaskTable;

import java.util.List;

/**
 * 更新任务列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Singleton
public class ResUpdateTaskListHandler {

    /** 更新任务列表 */
    @ProtoRequest
    public void resUpdateTaskList(SocketSession socketSession, ResUpdateTaskList req) {
        Robot robot = socketSession.bindData("robot");
        List<TaskBean> tasks = req.getTasks();
        QTaskTable taskTable = DataRepository.getIns().dataTable(QTaskTable.class);
        for (TaskBean task : tasks) {
            robot.getTasks().put(task.getTaskId(), task);
            log.info("{} 任务更新: {}, {}", robot, taskTable.get(task.getTaskId()).getInnerTaskDetail(), task);
        }
    }

}