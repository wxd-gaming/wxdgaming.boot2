package wxdgaming.boot2.core.cache2;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

@Getter
class CacheHolder<V> {

    private final V value;
    /** 最后执行心跳的时间 */

    @Setter private long lastHeartTime;
    /** 过期时间 */
    @Setter private long expireEndTime;

    public CacheHolder(V value) {
        this.value = value;
    }

    @Override public String toString() {
        return JSON.toJSONString(value);
    }
}
