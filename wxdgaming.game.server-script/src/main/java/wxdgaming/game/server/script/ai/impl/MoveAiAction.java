package wxdgaming.game.server.script.ai.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.RandomUtils;
import wxdgaming.game.server.bean.Vector3D;
import wxdgaming.game.server.bean.ai.AiAction;
import wxdgaming.game.server.bean.ai.AiActionData;
import wxdgaming.game.server.bean.ai.AiPanel;
import wxdgaming.game.server.bean.ai.AiType;
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
public class MoveAiAction extends AbstractAiAction implements InitPrint {

    public MoveAiAction() {
    }

    @Override public AiType aiType() {
        return AiType.Move;
    }

    @Override public void doAction(AiPanel aiPanel, AiActionData aiActionData) {
        List<Vector3D> targetPathList = aiPanel.getTargetPathList();
        if (aiActionData.getLastMoveTime() == 0) {
            aiActionData.updateLastMoveTime();
            return;
        }
        boolean diff = MyClock.millis() - aiActionData.getLastMoveTime() > 500;
        if (!diff) {
            return;
        }

        aiActionData.updateLastMoveTime();
        Vector3D vector3D = targetPathList.removeFirst();
        log.debug("{} ai 移动：{}, {}", aiPanel.getMapNpc(), vector3D, targetPathList.size());

        if (!targetPathList.isEmpty())
            return;

        /*根据实际情况 或许需要继续寻路，或者需要休息*/
        if (RandomUtils.randomBoolean()) {
            aiPanel.changeAiAction(AiAction.FindPath);
        } else {
            aiPanel.changeAiAction(AiAction.Idle);
        }
    }

    @Override public void doClose(AiPanel aiPanel) {

    }
}
