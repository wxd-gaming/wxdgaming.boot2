package wxdgaming.game.server.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-14 13:42
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "backend")
public class BackendConfig extends ObjectBase {

    private int gameId;
    private String url;
    private String appToken;
    private String logToken;

}
