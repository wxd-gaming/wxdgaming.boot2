package wxdgaming.boot2.starter.date.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.date.AbstractDateConvert;

/**
 * 当天的最大时间，23:59:59
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class CurrentDayMaxConvertImpl extends AbstractDateConvert implements InitPrint {

    @Override public String type() {
        return "CurrentDayMax";
    }

    @Override public long convert(String[] params) {
        int days = Integer.parseInt(params[1]);
        long time = MyClock.addDayOfTime(days);
        return MyClock.dayMaxTime(time);
    }

    @Override public long convertEndTime(long startTime, String[] params) {
        return convert(params);
    }

}
