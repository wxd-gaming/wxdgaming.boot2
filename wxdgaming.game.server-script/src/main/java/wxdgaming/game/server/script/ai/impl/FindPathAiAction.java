package wxdgaming.game.server.script.ai.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.util.RandomUtils;
import wxdgaming.game.server.bean.Vector3D;
import wxdgaming.game.server.bean.ai.AiAction;
import wxdgaming.game.server.bean.ai.AiActionData;
import wxdgaming.game.server.bean.ai.AiPanel;
import wxdgaming.game.server.bean.ai.AiType;
import wxdgaming.game.server.script.ai.AbstractAiAction;

import java.util.ArrayList;
import java.util.List;

/**
 * 寻路
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 13:57
 **/
@Slf4j
@Component
public class FindPathAiAction extends AbstractAiAction implements InitPrint {

    public FindPathAiAction() {
    }

    @Override public AiType aiType() {
        return AiType.FindPath;
    }

    @Override public void doAction(AiPanel aiPanel, AiActionData aiActionData) {
        if (RandomUtils.randomBoolean()) {
            /*无法寻路切换到休闲模式*/
            aiPanel.changeAiAction(AiAction.Idle);
        } else {
            Vector3D vector3D = new Vector3D(1, 1, 1);
            aiPanel.setTargetPoint(vector3D);
            List<Vector3D> list = new ArrayList<>();
            int random = RandomUtils.random(3, 15);
            for (int i = 0; i < random; i++) {
                list.add(vector3D);
            }
            aiPanel.setTargetPathList(list);
            log.debug("{} ai 寻路：{}", aiPanel.getMapNpc(), list);
            aiPanel.changeAiAction(AiAction.Move);
        }
    }

    @Override public void doClose(AiPanel aiPanel) {

    }
}
