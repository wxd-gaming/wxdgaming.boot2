package wxdgaming.game.server.bean.task;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;

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

    private long acceptTime;
    private boolean complete = false;
    /** 是否领取奖励 */
    private boolean rewards = false;
    /** key:完成条件, value:当前进度 */
    private HashMap<Integer, Long> progresses = new HashMap<>();

}
