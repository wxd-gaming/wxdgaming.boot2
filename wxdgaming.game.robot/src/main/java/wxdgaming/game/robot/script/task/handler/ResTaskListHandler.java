package wxdgaming.game.robot.script.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.cfg.QTaskTable;
import wxdgaming.game.message.task.ResTaskList;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.robot.bean.Robot;

import java.util.List;

/**
 * 任务列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResTaskListHandler {

    /** 任务列表 */
    @ProtoRequest(ResTaskList.class)
    public void resTaskList(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ResTaskList req = event.buildMessage();
        Robot robot = socketSession.bindData("robot");
        List<TaskBean> tasks = req.getTasks();
        QTaskTable taskTable = DataRepository.getIns().dataTable(QTaskTable.class);
        for (TaskBean task : tasks) {
            robot.getTasks().put(task.getTaskId(), task);
            log.info("{} 任务: {}", robot, taskTable.get(task.getTaskId()).getInnerTaskDetail());
        }
    }

}