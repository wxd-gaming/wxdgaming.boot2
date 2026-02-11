package wxdgaming.boot2.starter.date.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.date.IDateConvert;

/**
 * 从当前时间开始
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class CurrentConvertImpl implements InitPrint, IDateConvert {

    @Override public String type() {
        return "Current";
    }

    @Override public long convert(String date) {
        return MyClock.millis();
    }

    @Override public long convertEndTime(long startTime, String date) {
        return convert(date);
    }

}
