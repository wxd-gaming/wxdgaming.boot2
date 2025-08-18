package wxdgaming.logserver.bean;

import lombok.Getter;
import wxdgaming.boot2.core.cache2.CASCache;
import wxdgaming.boot2.core.format.HexId;

import java.util.concurrent.TimeUnit;

/**
 * 日志上下文
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 19:36
 **/
@Getter
public class LogTableContext {

    private final String logType;
    private final HexId hexId = new HexId(1);
    private final CASCache<Long, Boolean> logFilter;

    public LogTableContext(String logType) {
        this.logType = logType;
        logFilter = CASCache.<Long, Boolean>builder()
                .area(10)
                .heartTimeMs(TimeUnit.HOURS.toMillis(1))
                .expireAfterWriteMs(TimeUnit.HOURS.toMillis(24))
                .build();
        logFilter.start();
    }

    public long newId() {
        return hexId.newId();
    }

    public boolean filter(long uid) {
        return logFilter.has(uid);
    }

    public void addFilter(long uid) {
        logFilter.put(uid, true);
    }

}
