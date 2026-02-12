package wxdgaming.boot2.starter.date.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.date.AbstractDateConvert;

/**
 * 把分钟转换成毫秒
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class YYYYMMDDConvertImpl extends AbstractDateConvert implements InitPrint {

    public YYYYMMDDConvertImpl() {
    }

    @Override public String type() {
        return "yyyymmdd";
    }

    @Override public long convert(JSONObject extendParams, String[] params) {
        return MyClock.parseDate("yyyyMMdd", params[1]).getTime();
    }

    @Override public long convertEndTime(JSONObject extendParams, long startTime, String[] params) {
        return convert(extendParams, params) - 1000;
    }

}
