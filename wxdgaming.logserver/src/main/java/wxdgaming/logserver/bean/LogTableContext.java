package wxdgaming.logserver.bean;

import lombok.Getter;
import wxdgaming.boot2.core.cache.Cache;
import wxdgaming.boot2.core.cache.LRUCacheCAS;
import wxdgaming.boot2.core.format.HexId;

import java.time.Duration;

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
    private final Cache<Long, Boolean> logFilter;

    public LogTableContext(String logType) {
        this.logType = logType;
        logFilter = LRUCacheCAS.<Long, Boolean>builder()
                .blockSize(32)
                .heartExpireAfterWrite(Duration.ofHours(1))
                .expireAfterWrite(Duration.ofHours(24))
                .build();
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
