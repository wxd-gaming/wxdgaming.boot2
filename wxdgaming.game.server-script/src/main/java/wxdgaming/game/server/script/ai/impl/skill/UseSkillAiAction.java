package wxdgaming.game.server.script.ai.impl.skill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.server.bean.ai.AiPanel;
import wxdgaming.game.server.bean.ai.AiType;
import wxdgaming.game.server.bean.ai.UseSkillAiActionData;
import wxdgaming.game.server.script.ai.AbstractAiAction;

/**
 * 使用技能
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 20:03
 **/
@Slf4j
@Component
public class UseSkillAiAction extends AbstractAiAction<UseSkillAiActionData> {

    @Override public AiType aiType() {
        return AiType.UseSkill;
    }

    @Override public void doAction(AiPanel aiPanel, UseSkillAiActionData data) {

    }

    @Override public void doClose(AiPanel aiPanel) {

    }
}
