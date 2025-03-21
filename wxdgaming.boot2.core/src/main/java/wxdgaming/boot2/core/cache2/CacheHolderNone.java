package wxdgaming.boot2.core.cache2;

import lombok.Getter;
import lombok.Setter;

/** 空缓存 */
@Getter
class CacheHolderNone {

    /** 最后执行心跳的时间 */
    @Setter private long lastHeartTime;
    /** 过期时间 */
    @Setter private long expireEndTime;

    @Override public String toString() {
        return "true";
    }
}
