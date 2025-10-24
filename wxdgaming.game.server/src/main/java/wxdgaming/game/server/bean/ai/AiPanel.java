package wxdgaming.game.server.bean.ai;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.game.server.bean.MapNpc;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ai面板
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 15:35
 **/
@Slf4j
@Getter
@Setter
public class AiPanel extends ObjectBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private MapNpc mapNpc;
    /** 当前有效的ai */
    private TreeMap<Integer, AiActionData> aiMap = new TreeMap<>();

    /** ai模板 */
    private Map<AiAction, AiType> aiActionTemplateMap = new HashMap<>();

    public AiPanel(MapNpc mapNpc) {
        this.mapNpc = mapNpc;
        aiActionTemplateMap.put(AiAction.Idle, AiType.Idle);
        aiActionTemplateMap.put(AiAction.FindPath, AiType.FindPath);
        aiActionTemplateMap.put(AiAction.Move, AiType.Move);
        aiActionTemplateMap.put(AiAction.UseSkill, AiType.UseSkill);
    }

    public <D extends AiActionData> D changeAiAction(AiAction action) {
        AiActionData aiActionData = action.getNewData().apply(action);
        AiActionData oldActionData = aiMap.put(action.getGroup(), aiActionData);
        log.debug("{} 切换ai：{} -> {}", mapNpc, oldActionData, action);
        return (D) aiActionData;
    }

    @Override public String toString() {
        return "AiPanel{mapNpc=%s}".formatted(mapNpc);
    }

}
