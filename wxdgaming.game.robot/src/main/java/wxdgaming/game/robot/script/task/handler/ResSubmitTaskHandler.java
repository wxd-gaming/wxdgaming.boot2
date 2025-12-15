package wxdgaming.game.robot.script.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.cfg.QTaskTable;
import wxdgaming.game.message.task.ResSubmitTask;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.robot.bean.Robot;

/**
 * 提交任务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResSubmitTaskHandler {

    /** 提交任务 */
    @ProtoRequest(ResSubmitTask.class)
    public void resSubmitTask(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ResSubmitTask req = event.buildMessage();
        Robot robot = socketSession.bindData("robot");
        TaskBean taskBean = robot.getTasks().get(req.getTaskId());
        if (taskBean == null) {
            return;
        }

        QTaskTable taskTable = DataRepository.getIns().dataTable(QTaskTable.class);
        log.info("{} 完成任务: {}", robot, taskTable.getByKey(taskBean.getTaskId()).getInnerTaskDetail());

        taskBean.setReward(true);
        if (req.isRemove()) {
            robot.getTasks().remove(req.getTaskId());
        }
    }

}