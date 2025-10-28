package wxdgaming.game.server.script.ai.impl.move;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.server.bean.ai.AiAction;
import wxdgaming.game.server.bean.ai.AiActionData;
import wxdgaming.game.server.bean.ai.AiPanel;
import wxdgaming.game.server.bean.ai.AiType;
import wxdgaming.game.server.script.ai.AbstractAiAction;

import java.util.concurrent.TimeUnit;

/**
 * 空闲，休息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 13:57
 **/
@Slf4j
@Component
public class IdleAiAction extends AbstractAiAction<AiActionData> implements InitPrint {

    public IdleAiAction() {
    }

    @Override public AiType aiType() {
        return AiType.Idle;
    }

    @Override public void doAction(AiPanel aiPanel, AiActionData aiActionData) {
        if (MyClock.millis() - aiActionData.getStartTime() > TimeUnit.SECONDS.toMillis(5)) {
            /*模拟没事走两步的ai*/
            aiPanel.changeAiAction(AiAction.FindPath);
        }
    }

    @Override public void doClose(AiPanel aiPanel) {

    }
}
