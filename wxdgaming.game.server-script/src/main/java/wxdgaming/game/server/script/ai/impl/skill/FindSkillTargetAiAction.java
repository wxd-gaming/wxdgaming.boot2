package wxdgaming.game.server.script.ai.impl.skill;

import org.springframework.stereotype.Component;
import wxdgaming.game.server.bean.ai.AiPanel;
import wxdgaming.game.server.bean.ai.AiType;
import wxdgaming.game.server.bean.ai.UseSkillAiActionData;
import wxdgaming.game.server.script.ai.AbstractAiAction;

/**
 * 寻找技能目标
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 20:03
 **/
@Component
public class FindSkillTargetAiAction extends AbstractAiAction<UseSkillAiActionData> {

    @Override public AiType aiType() {
        return AiType.FindSkillTarget;
    }

    @Override public void doAction(AiPanel aiPanel, UseSkillAiActionData data) {

    }

    @Override public void doClose(AiPanel aiPanel) {

    }
}
