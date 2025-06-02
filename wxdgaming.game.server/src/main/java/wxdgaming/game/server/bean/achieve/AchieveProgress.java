package wxdgaming.game.server.bean.achieve;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 任务进度
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-02 10:11
 **/
@Getter
@Setter
public class AchieveProgress extends ObjectBase {

    private int cfgId;
    private long progress;

}
