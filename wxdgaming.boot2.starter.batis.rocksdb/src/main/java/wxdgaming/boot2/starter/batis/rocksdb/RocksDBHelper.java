package wxdgaming.boot2.starter.batis.rocksdb;

import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.rocksdb.*;
import wxdgaming.boot2.core.io.SerializerUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * rocksdb
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-18 09:38
 **/
@Getter
public class RocksDBHelper {

    static {
        // 初始化 RocksDB 库（建议在程序启动时调用）
        RocksDB.loadLibrary();
    }

    private final RocksDB db;

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

    public void put(String key, Object obj) {
        try {
            db.put(key.getBytes(StandardCharsets.UTF_8), wxdgaming.boot2.core.io.SerializerUtil.encode(obj));
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public void putAll(Map<String, ?> map) {
        try {
            // 批量写操作（提升多写性能）
            WriteBatch writeBatch = new WriteBatch();
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                writeBatch.put(entry.getKey().getBytes(StandardCharsets.UTF_8), wxdgaming.boot2.core.io.SerializerUtil.encode(entry.getValue()));
            }
            // 执行批量写
            db.write(new WriteOptions(), writeBatch);
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
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
        return SerializerUtil.decode(bytes, clazz);
    }

    public String getString(String key) {
        byte[] bytes = get(key);
        if (bytes == null) {
            return null;
        }
        return SerializerUtil.decode(bytes, String.class);
    }

    public Integer getInteger(String key) {
        byte[] bytes = get(key);
        if (bytes == null) {
            return null;
        }
        return SerializerUtil.decode(bytes, Integer.class);
    }

    public int getIntValue(String key) {
        byte[] bytes = get(key);
        if (bytes == null) {
            return 0;
        }
        return SerializerUtil.decode(bytes, Integer.class);
    }

    public void close() {
        db.close();
    }

}
