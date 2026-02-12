package wxdgaming.boot2.starter.date.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.starter.date.AbstractDateConvert;

import java.time.Duration;

/**
 * 把分钟转换成毫秒
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class MinuteConvertImpl extends AbstractDateConvert implements InitPrint {

    public MinuteConvertImpl() {
    }

    @Override public String type() {
        return "minute";
    }

    @Override public long convert(JSONObject extendParams, String[] params) {
        return Duration.ofMinutes(Integer.parseInt(params[1])).toMillis();
    }

    @Override public long convertEndTime(JSONObject extendParams, long startTime, String[] params) {
        return startTime + convert(extendParams, params);
    }
}
