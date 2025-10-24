package wxdgaming.game.server.script.ai.impl.move;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.RandomUtils;
import wxdgaming.game.server.bean.Vector3D;
import wxdgaming.game.server.bean.ai.AiAction;
import wxdgaming.game.server.bean.ai.AiPanel;
import wxdgaming.game.server.bean.ai.AiType;
import wxdgaming.game.server.bean.ai.MoveAiActionData;
import wxdgaming.game.server.script.ai.AbstractAiAction;

import java.util.List;

/**
 * 正常移动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 13:57
 **/
@Slf4j
@Component
public class MoveAiAction extends AbstractAiAction<MoveAiActionData> implements InitPrint {

    public MoveAiAction() {
    }

    @Override public AiType aiType() {
        return AiType.Move;
    }

    @Override public void doAction(AiPanel aiPanel, MoveAiActionData aiActionData) {
        List<Vector3D> targetPathList = aiActionData.getTargetPathList();
        if (aiActionData.getLastMoveTime() == 0) {
            aiActionData.updateLastMoveTime();
            return;
        }
        boolean diff = MyClock.millis() - aiActionData.getLastMoveTime() > 500;
        if (!diff) {
            return;
        }

        if (!targetPathList.isEmpty()) {
            aiActionData.updateLastMoveTime();
            Vector3D vector3D = targetPathList.removeFirst();
            log.debug("{} ai 移动：{}, {}", aiPanel.getMapNpc(), vector3D, targetPathList.size());
            return;
        }

        /*根据实际情况 或许需要继续寻路，或者需要休息*/
        if (RandomUtils.randomBoolean()) {
            /*继续寻路*/
            MoveAiActionData changeAiAction = aiPanel.changeAiAction(AiAction.FindPath);
            changeAiAction.setTargetPoint(new Vector3D(RandomUtils.random(1, 20), RandomUtils.random(1, 20), RandomUtils.random(1, 20)));
        } else {
            aiPanel.changeAiAction(AiAction.Idle);
        }
    }

    @Override public void doClose(AiPanel aiPanel) {

    }
}
