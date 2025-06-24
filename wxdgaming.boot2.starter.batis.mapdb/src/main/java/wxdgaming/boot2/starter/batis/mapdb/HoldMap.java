package wxdgaming.boot2.starter.batis.mapdb;

import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存持有类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-24 17:05
 **/
@Getter
public class HoldMap {

    private final ConcurrentMap<String, Object> hold;

    public HoldMap(ConcurrentMap<String, Object> hold) {
        this.hold = hold;
    }

    public boolean exists(String key) {
        return hold.containsKey(key);
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return hold.entrySet();
    }

    public Collection<Object> values() {
        return hold.values();
    }

    /**
     * 获取指定的键的值
     *
     * @param key 缓存键
     * @param <T> 对象
     * @return 如果不存在缓存返回null, 返回对应的值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) hold.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T remove(String key) {
        return (T) hold.remove(key);
    }

    /**
     * 添加或者覆盖指定的键
     *
     * @param key   缓存键
     * @param value 缓存的value
     * @param <T>   对象
     * @return 如果不存在缓存返回null, 如果已经存在放回上次的值
     */
    @SuppressWarnings("unchecked")
    public <T> T put(String key, T value) {
        return (T) hold.put(key, value);
    }

    public void put(Map<String, Object> map) {
        hold.putAll(map);
    }

    /**
     * 如果不存在往里面添加
     *
     * @param key   缓存键
     * @param value 缓存的value
     * @param <T>   对象
     * @return 如果不存在缓存返回null, 如果已经存在放回上次的值
     */
    @SuppressWarnings("unchecked")
    public <T> T putIfAbsent(String key, T value) {
        return (T) hold.putIfAbsent(key, value);
    }

}
