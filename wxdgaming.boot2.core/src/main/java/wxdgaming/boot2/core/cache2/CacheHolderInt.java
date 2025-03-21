package wxdgaming.boot2.core.cache2;

import lombok.Getter;
import lombok.Setter;

@Getter
class CacheHolderInt {

    private final int value;
    /** 最后执行心跳的时间 */

    @Setter private long lastHeartTime;
    /** 过期时间 */
    @Setter private long expireEndTime;

    public CacheHolderInt(int value) {
        this.value = value;
    }

    @Override public String toString() {
        return String.valueOf(value);
    }
}
