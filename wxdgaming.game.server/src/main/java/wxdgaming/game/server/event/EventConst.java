package wxdgaming.game.server.event;

import wxdgaming.boot2.core.Event;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.attribute.CalculatorType;
import wxdgaming.game.server.bean.reason.ReasonDTO;
import wxdgaming.game.server.bean.role.Player;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-04 11:35
 **/
public interface EventConst {

    public record CreatePlayerEvent(Player player) implements Event {}

    public record LoginBeforePlayerEvent(Player player) implements Event {}

    public record LoginPlayerEvent(Player player) implements Event {}

    public record LogoutPlayerEvent(Player player) implements Event {}

    public record LevelUpEvent(Player player, int changeLv) implements Event {}

    /**
     * 玩家属性计算
     *
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2025-05-09 20:38
     */
    public record PlayerAttributeCalculatorEvent(
            Player player,
            CalculatorType[] calculatorTypes,
            ReasonDTO reasonDTO) implements Event {
    }

    public record NpcAttributeCalculatorEvent(
            MapNpc npc,
            CalculatorType[] calculatorTypes,
            ReasonDTO msg) implements Event {
    }

}
