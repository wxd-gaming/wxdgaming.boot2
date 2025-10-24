package wxdgaming.game.server.script.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.InitEvent;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.ai.AiAction;
import wxdgaming.game.server.bean.ai.AiActionData;
import wxdgaming.game.server.bean.ai.AiPanel;
import wxdgaming.game.server.bean.ai.AiType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.EventConst;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ai服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-23 19:41
 **/
@Slf4j
@Service
public class AiService extends HoldApplicationContext {

    Map<AiType, AbstractAiAction> aiActionMap = Map.of();

    public AiService() {
    }

    @EventListener
    public void init(InitEvent event) {
        aiActionMap = event.applicationContextProvider().toMap(AbstractAiAction.class, AbstractAiAction::aiType);
    }

    public <D extends AiActionData, A extends AbstractAiAction<D>> A aiAction(AiType aiType) {
        return (A) aiActionMap.get(aiType);
    }

    @EventListener
    public void playerLoginEvent(EventConst.LoginPlayerEvent event) {
        Player player = event.player();
        player.setAiPanel(new AiPanel(player));
        AiPanel aiPanel = player.getAiPanel();
        aiPanel.changeAiAction(AiAction.Idle);
    }

    @EventListener
    public void heartEventAction(EventConst.MapNpcHeartEvent event) {
        MapNpc mapNpc = event.mapNpc();
        AiPanel aiPanel = mapNpc.getAiPanel();
        Collection<AiActionData> aiMap = List.copyOf(aiPanel.getAiMap().values());
        for (AiActionData aiActionData : aiMap) {
            AiType aiType = aiPanel.getAiActionTemplateMap().get(aiActionData.getAiAction());
            AssertUtil.isNull(aiType, "ai 处理器类型未指定：%s", aiActionData);
            AbstractAiAction<AiActionData> abstractAiAction = aiAction(aiType);
            AssertUtil.isNull(abstractAiAction, "ai 处理器为实现：%s-%s", aiActionData, aiType);
            abstractAiAction.doAction(aiPanel, aiActionData);
        }
    }

}
