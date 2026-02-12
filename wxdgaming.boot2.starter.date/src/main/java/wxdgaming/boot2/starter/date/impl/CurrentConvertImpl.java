package wxdgaming.boot2.starter.date.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.date.AbstractDateConvert;

import java.util.Arrays;

/**
 * 从当前时间开始
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class CurrentConvertImpl extends AbstractDateConvert implements InitPrint {

    @Override public String type() {
        return "Current";
    }

    @Override public long convert(String[] params) {
        long add = 0;
        if (params.length > 2) {
            String[] newParams = Arrays.copyOfRange(params, 1, params.length - 1);
            add = dateService.convert(newParams);
        }
        return MyClock.millis() + add;
    }

    @Override public long convertEndTime(long startTime, String[] params) {
        return convert(params);
    }

}
