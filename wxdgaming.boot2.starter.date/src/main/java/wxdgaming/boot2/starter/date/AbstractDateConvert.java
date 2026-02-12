package wxdgaming.boot2.starter.date;

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

    public abstract long convert(String[] params);

    public abstract long convertEndTime(long startTime, String[] params);

}
