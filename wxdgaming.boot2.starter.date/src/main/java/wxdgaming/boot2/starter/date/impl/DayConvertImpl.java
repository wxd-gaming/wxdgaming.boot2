package wxdgaming.boot2.starter.date.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.starter.date.AbstractDateConvert;
import wxdgaming.boot2.starter.date.DateService;

import java.time.Duration;

/**
 * 把指定天数转换成毫秒
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class DayConvertImpl extends AbstractDateConvert implements InitPrint {

    @Autowired
    public DayConvertImpl(DateService dateService) {
        super(dateService);
    }

    @Override public String type() {
        return "day";
    }

    @Override public long convert(JSONObject extendParams, String[] params) {
        return Duration.ofDays(Integer.parseInt(params[1])).toMillis();
    }

    @Override public long convertEndTime(JSONObject extendParams, long startTime, String[] params) {
        return startTime + convert(extendParams, params);
    }

}
