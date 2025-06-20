package wxdgaming.boot2.starter.batis.rdb;

import lombok.extern.slf4j.Slf4j;
import org.rocksdb.*;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.util.SingletonLockUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * RocksDB 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-20 09:54
 **/
@Slf4j
public class RocksDBDataHelper {

    static {
        // 初始化 RocksDB 本地库
        RocksDB.loadLibrary();
    }

    final RocksDB db;
    final Options options;

    public RocksDBDataHelper(RocksDBConfig rdbConfig) {
        this(
                new Options()
                        .setCreateIfMissing(rdbConfig.isCreateIfMissing())       // 自动创建数据库
                        .setMaxOpenFiles(rdbConfig.getMaxOpenFiles())          // 最大打开文件数
                        .setWriteBufferSize(rdbConfig.getWriteBufferSize()) // 64MB 写缓冲区
                        .setMaxWriteBufferNumber(rdbConfig.getMaxWriteBufferNumber())     // 写缓冲区数量
                        .setCompressionType(CompressionType.SNAPPY_COMPRESSION) // 压缩算法
                ,
                rdbConfig.getDbPath()
        );
    }

    public RocksDBDataHelper(Options options, String path) {
        // 配置数据库选项
        this.options = options;
        try {
            db = RocksDB.open(options, path);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        try {
            db.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        options.close();
    }

    public RocksIterator iterator() {
        return db.newIterator();
    }

    public void put2Json(String key, Object value) {
        try {
            db.put(key.getBytes(StandardCharsets.UTF_8), FastJsonUtil.toJSONString(value).getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String key, Object value) {
        try {
            db.put(key.getBytes(StandardCharsets.UTF_8), String.valueOf(value).getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public void putMap(Map<String, String> map) {
        try (WriteBatch batch = new WriteBatch()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                batch.put(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
            }
            // 提交批量写入
            db.write(new WriteOptions(), batch);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }


    public String get(String key) {
        try {
            byte[] bytes = db.get(key.getBytes(StandardCharsets.UTF_8));
            if (bytes == null) {
                return null;
            }
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }


    public void del(String key) {
        try {
            db.delete(key.getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 线程安全的 putIfAbsent 操作（类似 Redis SETNX）
     *
     * @return true 表示写入成功，false 表示 key 已存在
     */
    public boolean putIfAbsent(String key, String value) throws RocksDBException {
        SingletonLockUtil.lock(key);
        try {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            if (db.get(keyBytes) == null) {
                db.put(keyBytes, value.getBytes(StandardCharsets.UTF_8));
                return true;
            }
            return false;
        } finally {
            SingletonLockUtil.unlock(key);
        }
    }

    public boolean delIfAbsent(String key) {
        SingletonLockUtil.lock(key);
        try {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            if (db.get(keyBytes) != null) {
                db.delete(keyBytes);
                return true;
            }
            return false;
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        } finally {
            SingletonLockUtil.unlock(key);
        }
    }
}
