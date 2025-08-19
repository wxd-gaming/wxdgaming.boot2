package wxdgaming.boot2.starter.batis.mapdb;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import kotlin.jvm.functions.Function1;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.*;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.io.FileUtil;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * mapdb 辅助
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-23 16:43
 **/
@Slf4j
@Singleton
public class MapDBDataHelper implements AutoCloseable, InitPrint {

    final DB db;
    final ConcurrentHashMap<String, Object> openCacheMap = new ConcurrentHashMap<>();

    final File dbFile;

    @Inject
    public MapDBDataHelper(MapdbProperties mapdbProperties) {
        this(new File(mapdbProperties.getPath()));
    }

    public MapDBDataHelper(String dbPath) {
        this(new File(dbPath));
    }

    public MapDBDataHelper(File file) {
        FileUtil.mkdirs(file);
        this.dbFile = file;
        this.db = DBMaker.fileDB(file)
                .fileChannelEnable()
                .checksumHeaderBypass()
                //                .checksumStoreEnable()  // 启用完整校验
                //                .closeOnJvmShutdown()
                .fileMmapEnable()            // 启用内存映射（提升读性能）
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
        log.info("关闭 map db {}", this.dbFile);
    }

    public boolean exists(String cacheName) {
        return db.exists(cacheName);
    }

    @SuppressWarnings("unchecked")
    private <T> T cache(String cacheName, Function<String, T> function) {
        return (T) openCacheMap.computeIfAbsent(cacheName, function);
    }

    /** 基于hash的map对象，对于随机读写性能很不错 */
    public HoldMap hMap(String cacheName) {
        return hMap(cacheName, 1000, null);
    }

    @SuppressWarnings("unchecked")
    public HoldMap hMap(String cacheName, int expireStoreSize, Function1<String, Object> valueLoader) {
        return cache(
                cacheName,
                k -> {
                    DB.HashMapMaker<String, Object> hashMapMaker = db.hashMap(cacheName, Serializer.STRING, Serializer.JAVA);
                    if (valueLoader != null) {
                        hashMapMaker.valueLoader(valueLoader);
                    }
                    hashMapMaker.expireStoreSize(expireStoreSize);
                    return new HoldMap(hashMapMaker.createOrOpen());
                }
        );
    }

    public HoldMap bMap(String cacheName) {
        return bMap(cacheName, 32);
    }

    /** 基于B树的map对象，对于连续读写性能不错，随机较差 */
    @SuppressWarnings("unchecked")
    public HoldMap bMap(String cacheName, int maxNodeSize) {
        return cache(
                cacheName,
                k -> new HoldMap((BTreeMap<String, Object>) db.treeMap(cacheName, Serializer.STRING, Serializer.JAVA).maxNodeSize(maxNodeSize).createOrOpen())
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
                k -> db.atomicLong(cacheName).createOrOpen()
        );
    }

    public Atomic.Integer atomicInteger(String cacheName) {
        return cache(
                cacheName,
                k -> db.atomicInteger(cacheName).createOrOpen()
        );
    }

    public Atomic.String atomicString(String cacheName) {
        return cache(
                cacheName,
                k -> {
                    return db.atomicString(cacheName).createOrOpen();
                }
        );
    }

    public <T> Atomic.Var<T> atomicVar(String cacheName) {
        return cache(
                cacheName,
                k -> {
                    Serializer<T> java = Serializer.JAVA;
                    return db.atomicVar(cacheName, java).createOrOpen();
                }
        );
    }


}
