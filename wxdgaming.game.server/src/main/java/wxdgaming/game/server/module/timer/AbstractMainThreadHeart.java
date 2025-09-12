package wxdgaming.game.server.module.timer;

public abstract class AbstractMainThreadHeart {

    public abstract void heart();

    public abstract void heartSecond(int second);

    public abstract void heartMinute(int minute);

    public abstract void heartHour(int hour);

    public abstract void heartDayEnd();

    /** 每一周开启的凌晨，也就是周一凌晨时间 */
    public abstract void heartWeek(long weekFirstDayStartTime);

}
