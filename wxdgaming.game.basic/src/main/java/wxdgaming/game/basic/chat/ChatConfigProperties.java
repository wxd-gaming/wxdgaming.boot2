package wxdgaming.game.basic.chat;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
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
@ConfigurationProperties(prefix = "chat")
public class ChatConfigProperties extends ObjectBase {

    private String url;
    private String jwtKey;

}
