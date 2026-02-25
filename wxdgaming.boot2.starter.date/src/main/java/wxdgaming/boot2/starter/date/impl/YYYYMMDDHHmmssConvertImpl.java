package wxdgaming.boot2.starter.date.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.date.AbstractDateConvert;
import wxdgaming.boot2.starter.date.DateService;

/**
 * 具体的时间格式，精确到秒
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class YYYYMMDDHHmmssConvertImpl extends AbstractDateConvert implements InitPrint {

    public YYYYMMDDHHmmssConvertImpl(DateService dateService) {
        super(dateService);
    }

    @Override public String type() {
        return "yyyyMMddHHmmss";
    }

    @Override public long convert(JSONObject extendParams, String[] params) {
        return MyClock.parseDate("yyyyMMddHHmmss", params[1]).getTime();
    }

    @Override public long convertEndTime(JSONObject extendParams, long startTime, String[] params) {
        return convert(extendParams, params);
    }

}
