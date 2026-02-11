package wxdgaming.boot2.starter.date.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.date.IDateConvert;

/**
 * 具体的时间格式，精确到秒
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class YYYYMMDDHHmmssConvertImpl implements InitPrint, IDateConvert {

    public YYYYMMDDHHmmssConvertImpl() {
    }

    @Override public String type() {
        return "yyyyMMddHHmmss";
    }

    @Override public long convert(String date) {
        return MyClock.parseDate("yyyyMMddHHmmss", date).getTime();
    }

    @Override public long convertEndTime(long startTime, String date) {
        return convert(date);
    }

}
