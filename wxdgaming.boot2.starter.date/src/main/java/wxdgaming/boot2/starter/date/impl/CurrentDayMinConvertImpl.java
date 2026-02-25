package wxdgaming.boot2.starter.date.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.date.AbstractDateConvert;
import wxdgaming.boot2.starter.date.DateService;

/**
 * 当前时间的凌晨时间 00:00:00
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class CurrentDayMinConvertImpl extends AbstractDateConvert implements InitPrint {


    @Autowired
    public CurrentDayMinConvertImpl(DateService dateService) {
        super(dateService);
    }

    @Override public String type() {
        return "CurrentDayMin";
    }

    @Override public long convert(JSONObject extendParams, String[] params) {
        int days = Integer.parseInt(params[1]);
        long time = MyClock.addDayOfTime(days);
        return MyClock.dayMinTime(time);
    }

    @Override public long convertEndTime(JSONObject extendParams, long startTime, String[] params) {
        return convert(extendParams, params);
    }

}
