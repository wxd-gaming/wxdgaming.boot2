package wxdgaming.game.server.script.ai.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.game.server.bean.ai.AiActionData;
import wxdgaming.game.server.bean.ai.AiPanel;
import wxdgaming.game.server.bean.ai.AiType;
import wxdgaming.game.server.script.ai.AbstractAiAction;

/**
 * 固定线路的移动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 13:57
 **/
@Slf4j
@Component
public class PathMoveAiAction extends AbstractAiAction implements InitPrint {

    public PathMoveAiAction() {
    }

    @Override public AiType aiType() {
        return AiType.PathMove;
    }

    @Override public void doAction(AiPanel aiPanel, AiActionData aiActionData) {

    }

    @Override public void doClose(AiPanel aiPanel) {

    }
}
