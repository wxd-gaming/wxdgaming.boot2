package wxdgaming.boot2.starter.date.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.date.IDateConvert;

/**
 * 当天的最大时间，23:59:59
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class CurrentDayMaxConvertImpl implements InitPrint, IDateConvert {

    @Override public String type() {
        return "CurrentDayMax";
    }

    @Override public long convert(String date) {
        return MyClock.dayMaxTime();
    }

    @Override public long convertEndTime(long startTime, String date) {
        return convert(date);
    }

}
