package wxdgaming.boot2.core.executor;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 主线程 驱动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-12 14:49
 **/
@Slf4j
public class ThreadDrive extends Thread implements Runnable {

    private final ThreadDriveHandlerProxy driveHandlerProxy = new ThreadDriveHandlerProxy();

    public ThreadDrive(String name) {
        super(name);
    }

    public void setDriveHandler(ThreadDriveHandler driveHandler) {
        this.driveHandlerProxy.setDriveHandler(driveHandler);
    }

    @Override public void run() {
        int s = MyClock.getSecond();
        int m = MyClock.getMinute();
        int h = MyClock.getHour();
        int d = MyClock.days();
        long w = MyClock.weekFirstDay();

        while (!SpringUtil.exiting.get()) {
            try {
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(33));
                if (driveHandlerProxy.getDriveHandler() == null) continue;
                driveHandlerProxy.heart();
                int ts = MyClock.getSecond();
                if (ts == s) {continue;}
                s = ts;
                driveHandlerProxy.heartSecond(s);
                int tm = MyClock.getMinute();
                if (tm == m) {continue;}
                m = tm;
                driveHandlerProxy.heartMinute(m);
                int th = MyClock.getHour();
                if (th == h) continue;
                h = th;
                driveHandlerProxy.heartHour(h);
                int td = MyClock.days();
                if (td == d) {continue;}
                d = td;
                driveHandlerProxy.heartDayEnd();
                long tw = MyClock.weekFirstDay();
                if (tw != w) {
                    w = tw;
                    driveHandlerProxy.heartWeek(w);
                }
            } catch (Throwable throwable) {
                log.error("{}", this.getName(), throwable);
            }
        }
    }

}
