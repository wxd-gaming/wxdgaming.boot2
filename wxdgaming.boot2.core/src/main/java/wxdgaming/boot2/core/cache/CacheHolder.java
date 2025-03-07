package wxdgaming.boot2.core.cache;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.Objects;

@Getter
class CacheHolder<K, V> {

    private final K key;
    private final V value;
    /** 最后执行心跳的时间 */

    @Setter private long lastHeartTime;
    /** 过期时间 */
    @Setter private long expireEndTime;

    public CacheHolder(K key, V value, long heartTime) {
        this.key = key;
        this.value = value;
        this.lastHeartTime = MyClock.millis() + heartTime;
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        CacheHolder<?, ?> that = (CacheHolder<?, ?>) o;
        return Objects.equals(getKey(), that.getKey());
    }

    @Override public int hashCode() {
        return Objects.hashCode(getKey());
    }

    @Override public String toString() {
        return "CacheHolder{" + "k=" + key + '}';
    }
}
