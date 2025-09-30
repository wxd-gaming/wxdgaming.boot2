package wxdgaming.boot2.core.executor;

/**
 * 驱动执行器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-12 16:30
 **/
public interface HeartDriveHandler {

    default void heart(long millis) {}

    default void heartSecond(int second) {}

    default void heartMinute(int minute) {}

    default void heartHour(int hour) {}

    default void heartDayEnd(int dayOfYear) {}

    /** 每一周开启的凌晨，也就是周一凌晨时间 */
    default void heartWeek(long weekFirstDayStartTime) {}

}
