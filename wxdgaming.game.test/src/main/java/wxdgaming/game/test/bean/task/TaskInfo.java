package wxdgaming.game.test.bean.task;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * 任务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-15 17:04
 **/
@Getter
@Setter
public class TaskInfo {

    private int cfgId;

    private long acceptTime;
    private boolean complete = false;
    /** 是否领取奖励 */
    private boolean rewards = false;
    /** key:完成条件, value:当前进度 */
    private HashMap<Integer, Long> progresses = new HashMap<>();

}
