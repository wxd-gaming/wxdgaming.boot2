package wxdgaming.game.test.bean.task;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.collection.ints.IntIntObjectTable;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.batis.ann.DbColumn;

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
public class TaskPack extends ObjectBase {

    /** 任务完成ID key:任务类型, value: 完成的id集合 */
    @JSONField(ordinal = 11)
    @DbColumn(length = Integer.MAX_VALUE)
    private HashMap<Integer, ArrayList<Integer>> taskFinishList = new HashMap<>();
    /** key:任务类型, value: 任务列表 */
    @JSONField(ordinal = 12)
    @DbColumn(length = Integer.MAX_VALUE)
    private IntIntObjectTable<TaskInfo> tasks = new IntIntObjectTable<>();

}
