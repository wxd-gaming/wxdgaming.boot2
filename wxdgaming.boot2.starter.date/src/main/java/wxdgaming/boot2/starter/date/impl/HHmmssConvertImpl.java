package wxdgaming.boot2.starter.date.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.date.AbstractDateConvert;
import wxdgaming.boot2.starter.date.DateService;

import java.util.concurrent.TimeUnit;

/**
 * 配置时分秒，相当于每天固定的几点钟
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class HHmmssConvertImpl extends AbstractDateConvert implements InitPrint {

    public HHmmssConvertImpl(DateService dateService) {
        super(dateService);
    }

    @Override public String type() {
        return "HHmmss";
    }

    @Override public long convert(JSONObject extendParams, String[] params) {
        long time = MyClock.dayMinTime();

        String timeStr = params[1];

        // 验证输入格式
        if (timeStr == null || timeStr.length() != 6) {
            throw new IllegalArgumentException("HHmmss format must be 6 digits, got: " + timeStr);
        }

        try {
            // 解析小时、分钟、秒
            int hour = Integer.parseInt(timeStr.substring(0, 2));
            int minute = Integer.parseInt(timeStr.substring(2, 4));
            int second = Integer.parseInt(timeStr.substring(4, 6));

            // 验证时间范围
            if (hour < 0 || hour > 23) {
                throw new IllegalArgumentException("Hour must be between 0-23, got: " + hour);
            }
            if (minute < 0 || minute > 59) {
                throw new IllegalArgumentException("Minute must be between 0-59, got: " + minute);
            }
            if (second < 0 || second > 59) {
                throw new IllegalArgumentException("Second must be between 0-59, got: " + second);
            }

            // 转换为总秒数
            time += TimeUnit.HOURS.toMillis(hour);
            time += TimeUnit.MINUTES.toMillis(minute);
            time += TimeUnit.SECONDS.toMillis(minute);

            return time;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid HHmmss format, must contain only digits: " + timeStr, e);
        }
    }

    @Override public long convertEndTime(JSONObject extendParams, long startTime, String[] params) {
        return convert(extendParams, params);
    }

}
