package wxdgaming.boot2.starter.batis.mapdb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-19 15:54
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "db.mapdb")
public class MapdbProperties extends ObjectBase implements InitPrint {

    private String path;

}
