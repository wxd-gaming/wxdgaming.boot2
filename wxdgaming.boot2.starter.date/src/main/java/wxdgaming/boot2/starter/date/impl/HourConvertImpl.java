package wxdgaming.boot2.starter.date.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.starter.date.IDateConvert;

import java.time.Duration;

/**
 * 把指定小时转换成毫秒
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class HourConvertImpl implements InitPrint, IDateConvert {

    public HourConvertImpl() {
    }

    @Override public String type() {
        return "Hour";
    }

    @Override public long convert(String date) {
        return Duration.ofHours(Integer.parseInt(date)).toMillis();
    }

    @Override public long convertEndTime(long startTime, String date) {
        return startTime + convert(date);
    }
}
