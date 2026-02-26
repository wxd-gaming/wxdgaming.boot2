package wxdgaming.boot2.starter.date;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.InitEvent;

import java.util.Map;

/**
 * 时间服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:12
 **/
@Getter
@Slf4j
@Service
public class DateService extends HoldApplicationContext {

    private Map<String, AbstractDateConvert> convertMap = null;

    public DateService() {

    }

    @EventListener
    public void init(InitEvent event) {
        convertMap = getApplicationContextProvider().toMap(AbstractDateConvert.class, i -> i.type().toUpperCase(), v -> v);
    }

    /**
     * 时间转换, 根据配置的时间格式，转换成开始时间和结束时间的毫秒
     * <p>例如: cron#0 0 20 * * ?&minute#30 晚上8点开始，持续30分钟
     * <p>例如: yyyymmdd#20260211&yyyymmdd#20260331 表示从2026-02-11开始 到 2026-03-31结束
     * <p>例如: yyyymmdd#20260211&day#3 表示从2026-02-11开始 持续3天
     *
     * @param cfgString cron#0 0 20 * * ?&cron#0 0 20 * * ?
     * @return bool 当前是否有效，开始时间，结束时间
     */
    public DateExpression convertBeginAndEnd(JSONObject extendParams, String cfgString) {
        String[] split = cfgString.split("&");
        String cfg1 = split[0];
        String cfg2 = split[1];
        long startTime;
        long endTime;
        {
            String[] split1 = cfg1.split("#");
            String type = split1[0];
            AbstractDateConvert convert = getConvertMap().get(type.toUpperCase());
            if (convert == null) throw new IllegalArgumentException(type + " not found");
            startTime = convert.convert(extendParams, split1);
        }
        {
            String[] split1 = cfg2.split("#");
            String type = split1[0];
            AbstractDateConvert convert = getConvertMap().get(type.toUpperCase());
            if (convert == null) throw new IllegalArgumentException(type + " not found");
            endTime = convert.convertEndTime(extendParams, startTime, split1);
        }
        return new DateExpression(startTime, endTime);
    }

    /**
     * 时间转换, 根据配置的时间格式，转换成毫秒
     *
     * @param cfgString cron#0 0 20 * * ?
     * @return bool 当前是否有效，开始时间，结束时间
     */
    public long convert(JSONObject extendParams, String cfgString) {
        String[] params = cfgString.split("#");
        return convert(extendParams, params);
    }

    public long convert(JSONObject extendParams, String[] params) {
        String type = params[0];
        AbstractDateConvert convert = getConvertMap().get(type.toUpperCase());
        return convert.convert(extendParams, params);
    }

}
