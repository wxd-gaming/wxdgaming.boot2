package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.timer.MyClock;

import java.time.LocalDateTime;

/**
 * 主线程 驱动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-12 14:49
 **/
@Slf4j
public class HeartDrive {

    private final HeartDriveHandlerProxy driveHandlerProxy = new HeartDriveHandlerProxy();

    @Getter final String name;
    int s;
    int m;
    int h;
    int dayOfYear;
    long w;

    public HeartDrive(String name) {
        this.name = name;
        long millis = MyClock.millis();
        LocalDateTime localDateTime = MyClock.localDateTime(millis);
        s = localDateTime.getSecond();
        m = localDateTime.getMinute();
        h = localDateTime.getHour();
        dayOfYear = localDateTime.getDayOfYear();
        w = MyClock.weekFirstDay(millis);
    }

    public void setDriveHandler(HeartDriveHandler driveHandler) {
        this.driveHandlerProxy.setDriveHandler(driveHandler);
    }

    public void doHeart() {
        try {
            if (driveHandlerProxy.getDriveHandler() == null) return;
            long millis = MyClock.millis();
            LocalDateTime localDateTime = MyClock.localDateTime(millis);
            driveHandlerProxy.heart(millis);
            int ts = localDateTime.getSecond();
            if (ts == s) {return;}
            s = ts;
            driveHandlerProxy.heartSecond(s);
            int tm = localDateTime.getMinute();
            if (tm == m) {return;}
            m = tm;
            driveHandlerProxy.heartMinute(m);
            int th = localDateTime.getHour();
            if (th == h) return;
            h = th;
            driveHandlerProxy.heartHour(h);
            int td = localDateTime.getDayOfYear();
            if (td == dayOfYear) {return;}
            dayOfYear = td;
            driveHandlerProxy.heartDayEnd(dayOfYear);
            long tw = MyClock.weekFirstDay(millis);
            if (tw != w) {
                w = tw;
                driveHandlerProxy.heartWeek(w);
            }
        } catch (Throwable throwable) {
            log.error("{}", this.getName(), throwable);
        }
    }

}
