package wxdgaming.boot2.starter.batis.rocksdb;

import org.rocksdb.RocksDB;

/**
 * rocksdb
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-18 09:38
 **/
public class RocksDBHelper {

    static {
        // 初始化 RocksDB 库（建议在程序启动时调用）
        RocksDB.loadLibrary();
    }

}
