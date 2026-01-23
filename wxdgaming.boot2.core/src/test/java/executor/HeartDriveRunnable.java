package executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.timer.MyClock;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 心跳驱动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 17:17
 **/
@Slf4j
public class HeartDriveRunnable implements Runnable {

    @Getter final String name;
    HeartDriveHandlerProxy driveHandlerProxy = new HeartDriveHandlerProxy();
    final long delay;
    final TimeUnit unit;
    int s;
    int m;
    int h;
    int dayOfYear;
    long w;

    public HeartDriveRunnable(AbstractExecutorService abstractExecutorService, String name, long delay, TimeUnit unit) {
        this.name = name;
        this.delay = delay;
        this.unit = unit;

        long millis = MyClock.millis();
        LocalDateTime localDateTime = MyClock.localDateTime(millis);
        s = localDateTime.getSecond();
        m = localDateTime.getMinute();
        h = localDateTime.getHour();
        dayOfYear = localDateTime.getDayOfYear();
        w = MyClock.weekMinTime(millis);

        abstractExecutorService.scheduleWithFixedDelay(this, delay, delay, unit);
    }

    public void setDriveHandler(HeartDriveHandler driveHandler) {
        driveHandlerProxy.setDriveHandler(driveHandler);
    }

    @Override public void run() {
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
            long tw = MyClock.weekMinTime(millis);
            if (tw != w) {
                w = tw;
                driveHandlerProxy.heartWeek(w);
            }
        } catch (Exception e) {
            log.error("{}", this.getName(), e);
        }
    }

}
