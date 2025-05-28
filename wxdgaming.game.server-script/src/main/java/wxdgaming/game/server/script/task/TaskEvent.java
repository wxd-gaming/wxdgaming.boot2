package wxdgaming.game.server.script.task;

import lombok.Builder;
import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.io.Serializable;

/**
 * 任务事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 13:30
 **/
@Getter
@Builder(toBuilder = true)
public class TaskEvent extends ObjectBase {

    Serializable k1;
    Serializable k2;
    Serializable k3;
    long targetValue;

}
