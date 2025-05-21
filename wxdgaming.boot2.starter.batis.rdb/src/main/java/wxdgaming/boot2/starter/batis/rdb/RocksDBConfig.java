package wxdgaming.boot2.starter.batis.rdb;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-20 20:03
 **/
@Getter
@Setter
public class RocksDBConfig extends ObjectBase {
    private boolean createIfMissing = true;
    private int maxOpenFiles = 1000;
    private int writeBufferSize = 512 * 1024 * 1024;
    private int maxWriteBufferNumber = 10;
    private String dbPath = "target/rocksdb";
}
