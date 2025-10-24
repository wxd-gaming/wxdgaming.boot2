package wxdgaming.game.server.script.ai;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.server.bean.ai.AiActionData;
import wxdgaming.game.server.bean.ai.AiPanel;
import wxdgaming.game.server.bean.ai.AiType;

/**
 * 基类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 13:14
 **/
@Slf4j
public abstract class AbstractAiAction {

    public abstract AiType aiType();

    /** 执行 */
    public abstract void doAction(AiPanel aiPanel, AiActionData aiActionData);

    /** 关闭，切换到其它状态执行 */
    public abstract void doClose(AiPanel aiPanel);

}
