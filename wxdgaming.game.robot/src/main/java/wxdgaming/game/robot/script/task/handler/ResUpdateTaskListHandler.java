package wxdgaming.game.robot.script.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.cfg.QTaskTable;
import wxdgaming.game.message.task.ResUpdateTaskList;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.robot.bean.Robot;

import java.util.List;

/**
 * 更新任务列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUpdateTaskListHandler {

    /** 更新任务列表 */
    @ProtoRequest(ResUpdateTaskList.class)
    public void resUpdateTaskList(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ResUpdateTaskList req = event.buildMessage();
        Robot robot = socketSession.bindData("robot");
        List<TaskBean> tasks = req.getTasks();
        QTaskTable taskTable = DataRepository.getIns().dataTable(QTaskTable.class);
        for (TaskBean task : tasks) {
            robot.getTasks().put(task.getTaskId(), task);
            log.info("{} 任务更新: {}, {}", robot, taskTable.getByKey(task.getTaskId()).getInnerTaskDetail(), task);
        }
    }

}