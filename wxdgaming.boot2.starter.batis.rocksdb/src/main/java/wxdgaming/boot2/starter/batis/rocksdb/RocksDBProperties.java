package wxdgaming.boot2.starter.batis.rocksdb;

import lombok.Getter;
import lombok.Setter;
import org.rocksdb.CompressionType;
import org.rocksdb.Options;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.util.BytesUnit;

/**
 * 配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-19 15:54
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "db.rocksdb")
public class RocksDBProperties extends ObjectBase implements InitPrint {

    private String path;
    private boolean createIfMissing = true;
    private CompressionType compressionType = CompressionType.LZ4_COMPRESSION;
    private String writeBufferSize = "16m";
    private int maxOpenFiles = 512;

    public Options options() {
        Options options = new Options();
        // 数据库不存在则创建
        options.setCreateIfMissing(createIfMissing);
        // 启用 LZ4 压缩（兼顾性能和压缩率）
        options.setCompressionType(compressionType);
        // 设置内存表（MemTable）大小上限（16MB）
        options.setWriteBufferSize(BytesUnit.stringToBytes(writeBufferSize));
        // 最大打开文件数（适配多 SSTable 场景）
        options.setMaxOpenFiles(maxOpenFiles);
        return options;
    }

}
