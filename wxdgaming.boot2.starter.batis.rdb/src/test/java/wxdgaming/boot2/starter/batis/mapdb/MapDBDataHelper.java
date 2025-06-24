package wxdgaming.boot2.starter.batis.mapdb;

import lombok.extern.slf4j.Slf4j;
import org.mapdb.*;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * mapdb 辅助
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-23 16:43
 **/
@Slf4j
public class MapDBDataHelper implements AutoCloseable {

    final DB db;
    final ConcurrentHashMap<String, Object> openCacheMap = new ConcurrentHashMap<>();

    public MapDBDataHelper(String dbPath) {
        this(new File(dbPath));
    }

    public MapDBDataHelper(File file) {
        this.db = DBMaker.fileDB(file)
                .fileChannelEnable()
                .closeOnJvmShutdown()
                .fileMmapEnableIfSupported() // 启用内存映射（提升读性能）
                .fileMmapPreclearDisable()   // 禁用预清理（避免写时阻塞）
                .cleanerHackEnable()         // 启用清理黑客（提升 mmap 关闭可靠性）
                .make();
    }

    public DB db() {
        return db;
    }

    @Override public void close() {
        db.close();
    }

    public boolean exists(String cacheName) {
        return db.exists(cacheName);
    }

    public <T> T cache(String cacheName, Function<String, T> function) {
        return (T) openCacheMap.computeIfAbsent(cacheName, function);
    }

    /** 基于hash的map对象，对于随机读写性能很不错 */
    @SuppressWarnings("unchecked")
    public ConcurrentMap<String, Object> hTreeMap(String cacheName) {
        return cache(
                cacheName,
                k -> (HTreeMap<String, Object>) db.hashMap(cacheName, Serializer.STRING, Serializer.JAVA).createOrOpen()
        );
    }

    /** 基于B树的map对象，对于连续读写性能不错，随机较差 */
    @SuppressWarnings("unchecked")
    public ConcurrentMap<String, Object> bTreeMap(String cacheName) {
        return cache(
                cacheName,
                k -> (BTreeMap<String, Object>) db.treeMap(cacheName, Serializer.STRING, Serializer.JAVA).createOrOpen()
        );
    }


    @SuppressWarnings("unchecked")
    public Set<Object> hashSet(String cacheName) {
        return cache(
                cacheName,
                k -> (Set<Object>) db.hashSet(cacheName).createOrOpen()
        );
    }

    public Atomic.Long atomicLong(String cacheName) {
        return cache(
                cacheName,
                k -> {
                    return db.atomicLong(cacheName).createOrOpen();
                }
        );
    }

    public Atomic.Integer atomicInteger(String cacheName) {
        return cache(
                cacheName,
                k -> {
                    return db.atomicInteger(cacheName).createOrOpen();
                }
        );
    }


    public boolean hTreeMapExists(String cacheName, String key) {
        if (!db.exists(cacheName)) return false;
        return hTreeMap(cacheName).containsKey(key);
    }

    /**
     * 获取指定的键的值
     *
     * @param cacheName 缓存名词
     * @param key       缓存键
     * @param <T>       对象
     * @return 如果不存在缓存返回null, 返回对应的值
     */
    @SuppressWarnings("unchecked")
    public <T> T hTreeMapGet(String cacheName, String key) {
        if (!db.exists(cacheName)) return null;
        return (T) hTreeMap(cacheName).get(key);
    }

    /**
     * 添加或者覆盖指定的键
     *
     * @param cacheName 缓存名词
     * @param key       缓存键
     * @param value     缓存的value
     * @param <T>       对象
     * @return 如果不存在缓存返回null, 如果已经存在放回上次的值
     */
    @SuppressWarnings("unchecked")
    public <T> T hTreeMapSet(String cacheName, String key, T value) {
        ConcurrentMap<String, Object> cache = hTreeMap(cacheName);
        return (T) cache.put(key, value);
    }

    /**
     * 如果不存在往里面添加
     *
     * @param cacheName 缓存名词
     * @param key       缓存键
     * @param value     缓存的value
     * @param <T>       对象
     * @return 如果不存在缓存返回null, 如果已经存在放回上次的值
     */
    @SuppressWarnings("unchecked")
    public <T> T hTreeMapSetIfAbsent(String cacheName, String key, Object value) {
        ConcurrentMap<String, Object> cache = hTreeMap(cacheName);
        return (T) cache.putIfAbsent(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T hTreeMapDel(String cacheName, String key) {
        if (!db.exists(cacheName)) return null;
        ConcurrentMap<String, Object> cache = hTreeMap(cacheName);
        return (T) cache.remove(key);
    }

}
