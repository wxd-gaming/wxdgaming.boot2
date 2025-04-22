package wxdgaming.game.test.bean.task;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
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

    /** 任务完成ID key:任务类型, value: 完成的id集合 */
    private HashMap<Integer, ArrayList<Integer>> taskFinishList = new HashMap<>();
    /** key:任务类型, value: 任务列表 */
    private ConcurrentTable<Integer, Integer, TaskInfo> tasks = new ConcurrentTable<>();

}
