package wxdgaming.game.test.bean.task;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.EntityLongUID;
import wxdgaming.boot2.starter.batis.ann.DbTable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 任务管理器 包装
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-15 17:03
 **/
@Getter
@Setter
@DbTable
public class TaskPack extends EntityLongUID {

    /** key:任务类型, value: 任务列表 */
    private HashMap<Integer, ArrayList<TaskInfo>> tasks = new HashMap<>();

}
