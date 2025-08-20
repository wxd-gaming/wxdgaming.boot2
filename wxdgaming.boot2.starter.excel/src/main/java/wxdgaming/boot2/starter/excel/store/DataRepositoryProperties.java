package wxdgaming.boot2.starter.excel.store;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.ann.Configuration;
import wxdgaming.boot2.core.ann.ConfigurationProperties;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 数据库仓库配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-19 17:10
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "data.json")
public class DataRepositoryProperties extends ObjectBase implements InitPrint {
    private String path;
    private String scan;
}
