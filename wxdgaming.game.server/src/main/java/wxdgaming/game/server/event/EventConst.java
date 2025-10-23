package wxdgaming.game.server.event;

import wxdgaming.boot2.core.event.Event;
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

    public interface MapNpcEvent extends Event {

        MapNpc mapNpc();

        default boolean isPlayer() {
            return mapNpc() instanceof Player;
        }

        default Player player() {
            return (Player) mapNpc();
        }

    }

    public record MapNpcHeartEvent(MapNpc mapNpc) implements MapNpcEvent {}

    public record MapNpcHeartSecondEvent(MapNpc mapNpc, int second) implements MapNpcEvent {}

    public record MapNpcHeartMinuteEvent(MapNpc mapNpc, int minute) implements MapNpcEvent {}

    public record MapNpcHeartHourEvent(MapNpc mapNpc, int hour) implements MapNpcEvent {}

    /** 跨天 */
    public record MapNpcHeartDayEvent(MapNpc mapNpc, int dayOfYear) implements MapNpcEvent {}

    /** 跨周 */
    public record MapNpcHeartWeekEvent(MapNpc mapNpc, long weekFirstDayStartTime) implements MapNpcEvent {}

    public record MapNpcAttributeCalculatorEvent(
            MapNpc mapNpc,
            CalculatorType[] calculatorTypes,
            ReasonDTO reasonDTO) implements MapNpcEvent {
    }

    public record ServerHeartEvent() implements Event {}

    public record ServerHeartSecondEvent(int second) implements Event {}

    public record ServerHeartMinuteEvent(int minute) implements Event {}

    public record ServerHeartHourEvent(int hour) implements Event {}

    /** 跨天 */
    public record ServerHeartDayEvent(int dayOfYear) implements Event {}

    /**
     * 跨周
     *
     * @param weekFirstDayStartTime 当前周开始的时间，默认是周一的凌晨
     */
    public record ServerHeartWeekEvent(long weekFirstDayStartTime) implements Event {}

}
