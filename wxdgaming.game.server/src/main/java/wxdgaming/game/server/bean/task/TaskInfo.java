package wxdgaming.game.server.bean.task;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.cfg.QTaskTable;
import wxdgaming.game.cfg.bean.QTask;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-15 17:04
 **/
@Getter
@Setter
@Accessors(chain = true)
public class TaskInfo extends ObjectBase {

    private int cfgId;
    /** 接取任务的时间 */
    private long acceptTime;
    /** 是否完成 */
    private boolean complete = false;
    /** 是否领取奖励 */
    private boolean rewards = false;
    /** 当前进度 */
    private HashMap<Integer, Long> progresses = new HashMap<>();

    public boolean update(Condition condition) {
        boolean updateing = false;
        QTask qTask = qTask();
        List<Condition> qTaskConditionList = qTask.getConditionList();

        for (int i = 0; i < qTaskConditionList.size(); i++) {
            Condition qCondition = qTaskConditionList.get(i);
            if (qCondition.equals(condition)) {
                long old = progresses.getOrDefault(i, 0L);
                long progress = condition.getUpdate().update(old, condition.getTarget());
                progresses.put(i, progress);
                updateing |= progress != old;
            }
        }

        if (updateing) {
            boolean checkComplete = true;
            for (int i = 0; i < qTaskConditionList.size(); i++) {
                Condition qCondition = qTaskConditionList.get(i);
                long progress = progresses.getOrDefault(i, 0L);
                if (qCondition.getTarget() > progress) {
                    checkComplete = false;
                    break;
                }
            }
            complete = checkComplete;
        }
        return updateing;
    }

    public QTask qTask() {
        return DataRepository.getIns().dataTable(QTaskTable.class, cfgId);
    }

    public TaskBean buildTaskBean() {
        TaskBean taskBean = new TaskBean();
        taskBean.setTaskId(this.getCfgId());
        taskBean.setAccept(this.getAcceptTime() > 0);
        taskBean.setCompleted(this.isComplete());
        if (!this.isComplete()) {
            HashMap<Integer, Long> progresses = this.getProgresses();
            progresses.entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .map(Map.Entry::getValue)
                    .forEach(taskBean.getProgresses()::add);
        }
        taskBean.setReward(this.isRewards());
        return taskBean;
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TaskInfo taskInfo = (TaskInfo) o;
        return getCfgId() == taskInfo.getCfgId();
    }

    @Override public int hashCode() {
        return getCfgId();
    }
}
