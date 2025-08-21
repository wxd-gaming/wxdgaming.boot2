package wxdgaming.logserver.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 15:43
 **/
@Getter
@Setter
public class LogEntityList {

    private long time;
    private String sign;
    private List<LogEntity> data;

}
