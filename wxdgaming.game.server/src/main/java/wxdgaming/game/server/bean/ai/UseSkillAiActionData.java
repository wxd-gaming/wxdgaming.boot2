package wxdgaming.game.server.bean.ai;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.server.bean.MapNpc;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 当前处理器数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 16:59
 **/
@Getter
@Setter
public class UseSkillAiActionData extends AiActionData implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private Object skill;
    private List<MapNpc> targetList = null;

    public UseSkillAiActionData(AiAction aiAction) {
        super(aiAction);
    }

    public void randomSkill() {
    }

}
