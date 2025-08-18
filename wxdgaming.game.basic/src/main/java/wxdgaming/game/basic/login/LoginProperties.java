package wxdgaming.game.basic.login;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.ann.Configuration;
import wxdgaming.boot2.core.ann.ConfigurationProperties;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 链接登录服务器配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-07 18:08
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "login")
public class LoginProperties extends ObjectBase {

    private String url;
    private String jwtKey;
    private int maxOnlineSize = 1000;

}
