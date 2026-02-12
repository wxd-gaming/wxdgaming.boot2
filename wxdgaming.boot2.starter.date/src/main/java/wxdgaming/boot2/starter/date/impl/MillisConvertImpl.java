package wxdgaming.boot2.starter.date.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.starter.date.AbstractDateConvert;

/**
 * 毫秒
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class MillisConvertImpl extends AbstractDateConvert implements InitPrint {

    public MillisConvertImpl() {
    }

    @Override public String type() {
        return "Millis";
    }

    @Override public long convert(JSONObject extendParams, String[] params) {
        return Long.parseLong(params[1]);
    }

    @Override public long convertEndTime(JSONObject extendParams, long startTime, String[] params) {
        return startTime + convert(extendParams, params);
    }
}
