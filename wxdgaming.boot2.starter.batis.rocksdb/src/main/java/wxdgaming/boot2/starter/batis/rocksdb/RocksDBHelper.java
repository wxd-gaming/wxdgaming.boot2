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

    public boolean exits(String key) {
        return db.keyExists(key.getBytes(StandardCharsets.UTF_8));
    }

    public void putMap(String key, Map<?, ?> map) {
        try {
            db.put(key.getBytes(StandardCharsets.UTF_8), kryoPool.serialize(map));
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public void put(String key, Object obj) {
        try {
            db.put(key.getBytes(StandardCharsets.UTF_8), kryoPool.serialize(obj));
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public void putByComboKey(Object obj, Object... ks) {
        try {
            String key = Joiner.on(":").join(ks);
            db.put(key.getBytes(StandardCharsets.UTF_8), kryoPool.serialize(obj));
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

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

    public byte[] getByComboKey(Object... ks) {
        String key = Joiner.on(":").join(ks);
        return get(key);
    }

    public <R> R getObjectByComboKey(Class<R> clazz, Object... ks) {
        byte[] bytes = getByComboKey(ks);
        if (bytes == null) {
            return null;
        }
        return kryoPool.deserialize(bytes, clazz);
    }

    public byte[] get(String key) {
        try {
            return db.get(key.getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public <R> R getObject(String key, Class<R> clazz) {
        byte[] bytes = get(key);
        if (bytes == null) {
            return null;
        }
        return kryoPool.deserialize(bytes, clazz);
    }

    public Map<?, ?> getMap(String key) {
        return getObject(key, Map.class);
    }

    public ConcurrentHashMap<?, ?> getConcurrentHashMap(String key) {
        return getObject(key, ConcurrentHashMap.class);
    }

    public String getString(String key) {
        byte[] bytes = get(key);
        if (bytes == null) {
            return null;
        }
        return kryoPool.deserialize(bytes, String.class);
    }

    public Integer getInteger(String key) {
        byte[] bytes = get(key);
        if (bytes == null) {
            return null;
        }
        return kryoPool.deserialize(bytes, Integer.class);
    }

    public int getIntValue(String key) {
        byte[] bytes = get(key);
        if (bytes == null) {
            return 0;
        }
        return kryoPool.deserialize(bytes, Integer.class);
    }

    public void close() {
        db.close();
    }

}
