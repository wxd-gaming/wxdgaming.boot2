package wxdgaming.boot2.starter.date;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 时间格式转换
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:13
 **/
@Getter
public abstract class AbstractDateConvert {

    @Autowired protected DateService dateService;

    public abstract String type();

    public abstract long convert(JSONObject extendParams, String[] params);

    public abstract long convertEndTime(JSONObject extendParams, long startTime, String[] params);

}
