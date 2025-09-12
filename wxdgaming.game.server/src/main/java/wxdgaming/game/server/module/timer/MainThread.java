package wxdgaming.game.server.module.timer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.server.event.OnServerHeart;
import wxdgaming.game.server.event.OnServerHeartMinute;
import wxdgaming.game.server.event.OnServerHeartSecond;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 主线程
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-12 14:49
 **/
@Slf4j
@Component
public class MainThread extends HoldApplicationContext implements Runnable {

    AbstractMainThreadHeart abstractMainThreadHeart;

    @Init
    @Order(-100)
    public void init() {
        abstractMainThreadHeart = getApplicationContextProvider().instance(AbstractMainThreadHeart.class);
    }

    @Start
    public void start() {
        new Thread(this, "MainThread").start();
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
                getApplicationContextProvider().executeMethodWithAnnotated(OnServerHeart.class);
                int ts = MyClock.getSecond();
                if (ts == s) {continue;}
                s = ts;
                getApplicationContextProvider().executeMethodWithAnnotated(OnServerHeartSecond.class);
                int tm = MyClock.getMinute();
                if (tm == m) {continue;}
                m = tm;
                getApplicationContextProvider().executeMethodWithAnnotated(OnServerHeartMinute.class);
                int th = MyClock.getHour();
                if (th == h) continue;
                h = th;
                abstractMainThreadHeart.heartHour(h);
                int td = MyClock.days();
                if (td == d) {continue;}
                d = td;
                abstractMainThreadHeart.heartDayEnd();
                long tw = MyClock.weekFirstDay();
                if (tw != w) {
                    w = tw;
                    abstractMainThreadHeart.heartWeek(w);
                }
            } catch (Throwable throwable) {
                log.error("MainThread error", throwable);
            }
        }
    }

}
