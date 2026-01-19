package wxdgaming.boot2.starter.batis.rocksdb;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.rocksdb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.io.kryoPool;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rocksdb
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-18 09:38
 **/
@Slf4j
@Getter
@Service
public class RocksDBHelper implements InitPrint {

    private static final kryoPool kryoPool = new kryoPool();

    static {
        // 初始化 RocksDB 库（建议在程序启动时调用）
        RocksDB.loadLibrary();
    }

    private final RocksDB db;

    @Autowired
    public RocksDBHelper(RocksDBProperties rocksDBProperties) {
        this(rocksDBProperties.options(), rocksDBProperties.getPath());
        log.debug("RocksDBHelper init: {}", rocksDBProperties.getPath());
    }

    public RocksDBHelper(String dbPath) {
        this(null, dbPath);
    }

    public RocksDBHelper(Options options, String dbPath) {
        if (options == null) {
            // 1. 配置数据库选项
            options = new Options();
            // 数据库不存在则创建
            options.setCreateIfMissing(true);
            // 启用 LZ4 压缩（兼顾性能和压缩率）
            options.setCompressionType(CompressionType.LZ4_COMPRESSION);
            // 设置内存表（MemTable）大小上限（16MB）
            options.setWriteBufferSize(16 * 1024 * 1024);
            // 最大打开文件数（适配多 SSTable 场景）
            options.setMaxOpenFiles(1000);
        }
        try {
            db = RocksDB.open(options, dbPath);
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    /** 模拟 redis 方式格式化 */
    byte[] comboKey(Object... ks) {
        String key = Joiner.on(":").join(ks);
        return key.getBytes(StandardCharsets.UTF_8);
    }

    public boolean exits(String key) {
        return db.keyExists(key.getBytes(StandardCharsets.UTF_8));
    }

    /** 写入单个数据 */
    public void put(String key, Object value) {
        try {
            db.put(key.getBytes(StandardCharsets.UTF_8), kryoPool.serialize(value));
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    /** 用复合主键的形式写入日志 */
    public void putByComboKey(Object value, Object... ks) {
        try {
            db.put(comboKey(ks), kryoPool.serialize(value));
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    /** 批量写入数据 */
    public void putAll(Map<String, ?> map) {
        try {
            // 批量写操作（提升多写性能）
            WriteBatch writeBatch = new WriteBatch();
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                writeBatch.put(entry.getKey().getBytes(StandardCharsets.UTF_8), kryoPool.serialize(entry.getValue()));
            }
            // 执行批量写
            db.write(new WriteOptions(), writeBatch);
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    /**
     * 通过复合主键获取一个值
     *
     * @param ks 复合主键
     * @return 值，如果值不存在，则返回 null
     */
    public byte[] getByComboKey(Object... ks) {
        byte[] bytes = comboKey(ks);
        return get(bytes);
    }

    /**
     * 通过复合主键获取一个值
     *
     * @param valueType 值的类型
     * @param ks        复合主键
     * @param <R>       值类型
     * @return 值，如果值不存在，则返回 null
     */
    public <R> R getObjectByComboKey(Class<R> valueType, Object... ks) {
        byte[] bytes = getByComboKey(ks);
        if (bytes == null) {
            return null;
        }
        return kryoPool.deserialize(bytes, valueType);
    }

    /** 获取一个值 */
    public byte[] get(String key) {
        return get(key.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] get(byte[] key) {
        try {
            return db.get(key);
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public String getString(String key) {
        byte[] bytes = get(key);
        if (bytes == null) {
            return null;
        }
        return kryoPool.deserialize(bytes, String.class);
    }

    /** 获取一个对象 */
    public <R> R getObject(String key, Class<R> clazz) {
        byte[] bytes = get(key);
        if (bytes == null) {
            return null;
        }
        return kryoPool.deserialize(bytes, clazz);
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getMap(String key) {
        return getObject(key, Map.class);
    }

    @SuppressWarnings("unchecked")
    public <R> List<R> getList(String key) {
        return getObject(key, List.class);
    }

    @SuppressWarnings("unchecked")
    public <K, V> ConcurrentHashMap<K, V> getConcurrentHashMap(String key) {
        return getObject(key, ConcurrentHashMap.class);
    }

    public Integer getInteger(String key) {
        return getObject(key, Integer.class);
    }

    public int getIntValue(String key) {
        Integer integer = getInteger(key);
        if (integer == null) {
            return 0;
        }
        return integer;
    }

    public Long getLong(String key) {
        return getObject(key, Long.class);
    }

    public long getLongValue(String key) {
        Long l = getLong(key);
        if (l == null) {
            return 0;
        }
        return l;
    }

    public void close() {
        db.close();
    }

}
