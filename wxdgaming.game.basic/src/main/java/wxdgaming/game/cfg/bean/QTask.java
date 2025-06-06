package wxdgaming.game.cfg.bean;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import org.apache.logging.log4j.message.ParameterizedMessage;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.cfg.QTaskTable;
import wxdgaming.game.cfg.bean.mapping.QTaskMapping;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 任务集合, src/cfg/任务成就.xlsx, q_task,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-03 15:28:21
 **/
@Getter
public class QTask extends QTaskMapping implements Serializable, DataChecked {

    private transient QTask qTaskBefore;
    private transient QTask qTaskAfter;
    /** 详情 */
    @JSONField(serialize = false)
    private transient String innerTaskDetail;
    /** 任务名字 */
    @JSONField(serialize = false)
    private transient String innerTaskName;
    /** 任务描述 */
    @JSONField(serialize = false)
    private transient String innerTaskDescription;

    @Override public void initAndCheck(Map<Class<?>, DataTable<?>> store) throws Exception {
        /*todo 实现数据检测和初始化*/
        Object[] objects = new Object[conditionList.size()];
        for (int i = 0; i < conditionList.size(); i++) {
            Condition condition = conditionList.get(i);
            objects[i] = condition.getTarget();
        }
        innerTaskDescription = new ParameterizedMessage(getDescription(), objects).getFormattedMessage();
        innerTaskName = String.format("%s(%s)", getId(), getName());
        innerTaskDetail = innerTaskName + "{" + innerTaskDescription + "}";

        DataTable<?> dataTable = store.get(QTaskTable.class);
        qTaskBefore = (QTask) dataTable.get(getBefore());
        qTaskAfter = (QTask) dataTable.get(getAfter());

    }

}
