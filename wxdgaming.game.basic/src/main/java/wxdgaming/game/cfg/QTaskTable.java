package wxdgaming.game.cfg;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.boot2.starter.excel.store.Keys;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.cfg.bean.QTask;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * excel 构建 任务集合, src/cfg/任务成就.xlsx, q_task,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-03 15:28:21
 **/
@Getter
public class QTaskTable extends DataTable<QTask> implements Serializable {

    private HashMap<TaskType, TreeMap<Integer, QTask>> taskGroupMap;

    @Override public void initDb() {
        /*todo 实现一些数据分组*/
        HashMap<TaskType, TreeMap<Integer, QTask>> tmpTasks = new HashMap<>();
        List<QTask> dataList = getDataList();
        for (QTask qTask : dataList) {
            tmpTasks.computeIfAbsent(qTask.getTaskType(), k -> new TreeMap<>())
                    .put(qTask.getId(), qTask);
        }
        taskGroupMap = tmpTasks;
    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}