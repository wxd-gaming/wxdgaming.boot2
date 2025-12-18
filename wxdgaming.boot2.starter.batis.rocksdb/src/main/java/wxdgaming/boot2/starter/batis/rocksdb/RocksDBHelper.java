package wxdgaming.boot2.starter.batis.rocksdb;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.rocksdb.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rocksdb
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-18 09:38
 **/
@Getter
public class RocksDBHelper {

    private static final ArrayBlockingQueue<Kryo> KRYO_THREAD_LOCAL;

    static {
        // 初始化 RocksDB 库（建议在程序启动时调用）
        RocksDB.loadLibrary();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        KRYO_THREAD_LOCAL = new ArrayBlockingQueue<>(availableProcessors);
        for (int i = 0; i < availableProcessors; i++) {
            Kryo e = new Kryo();
            /*开放式序列化*/
            e.setRegistrationRequired(false);
            KRYO_THREAD_LOCAL.add(e);
        }
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

    // 序列化：Object → byte[]
    public static <T> byte[] serialize(T obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Output output = new Output(baos)) { // Output 实现 AutoCloseable
            Kryo kryo = KRYO_THREAD_LOCAL.take();
            try {
                kryo.writeObject(output, obj);
                output.flush();
            } finally {
                KRYO_THREAD_LOCAL.add(kryo);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Kryo 序列化失败", e);
        }
    }

    // 反序列化：byte[] → Object
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             Input input = new Input(bais)) { // Input 实现 AutoCloseable
            Kryo kryo = KRYO_THREAD_LOCAL.take();
            try {
                return kryo.readObject(input, clazz);
            } finally {
                KRYO_THREAD_LOCAL.add(kryo);
            }
        } catch (Exception e) {
            throw new RuntimeException("Kryo 反序列化失败", e);
        }
    }

    public void putMap(String key, Map<?, ?> map) {
        try {
            db.put(key.getBytes(StandardCharsets.UTF_8), serialize(map));
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public void put(String key, Object obj) {
        try {
            db.put(key.getBytes(StandardCharsets.UTF_8), serialize(obj));
        } catch (RocksDBException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public void putAll(Map<String, ?> map) {
        try {
            // 批量写操作（提升多写性能）
            WriteBatch writeBatch = new WriteBatch();
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                writeBatch.put(entry.getKey().getBytes(StandardCharsets.UTF_8), serialize(entry.getValue()));
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
        return deserialize(bytes, clazz);
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
        return deserialize(bytes, String.class);
    }

    public Integer getInteger(String key) {
        byte[] bytes = get(key);
        if (bytes == null) {
            return null;
        }
        return deserialize(bytes, Integer.class);
    }

    public int getIntValue(String key) {
        byte[] bytes = get(key);
        if (bytes == null) {
            return 0;
        }
        return deserialize(bytes, Integer.class);
    }

    public void close() {
        db.close();
    }

}
