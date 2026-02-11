package wxdgaming.boot2.starter.date.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.date.IDateConvert;

/**
 * 当前时间的凌晨时间 00:00:00
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class CurrentDayMinConvertImpl implements InitPrint, IDateConvert {

    @Override public String type() {
        return "CurrentDayMin";
    }

    @Override public long convert(String date) {
        return MyClock.dayMinTime();
    }

    @Override public long convertEndTime(long startTime, String date) {
        return convert(date);
    }

}
