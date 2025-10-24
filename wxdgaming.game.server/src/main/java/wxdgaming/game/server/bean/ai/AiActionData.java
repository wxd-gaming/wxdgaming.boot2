package wxdgaming.game.server.bean.ai;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.timer.MyClock;

import java.io.Serial;
import java.io.Serializable;

/**
 * 当前处理器数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 16:59
 **/
@Getter
@Setter
public class AiActionData extends ObjectBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private final AiAction aiAction;
    private final long startTime;
    private long lastMoveTime;

    public AiActionData(AiAction aiAction) {
        this.aiAction = aiAction;
        this.startTime = MyClock.millis();
    }

    public void updateLastMoveTime() {
        lastMoveTime = MyClock.millis();
    }

    @Override public String toString() {
        return aiAction.name();
    }
}
