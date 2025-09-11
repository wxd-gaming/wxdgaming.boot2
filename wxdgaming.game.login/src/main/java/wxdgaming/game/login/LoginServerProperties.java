package wxdgaming.game.login;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.core.BootstrapProperties;
import wxdgaming.boot2.core.ann.Comment;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 11:08
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "boot")
public class LoginServerProperties extends BootstrapProperties {
    
    @Comment("管理员账号")
    private String adminName;
    @Comment("管理员密码")
    private String adminPwd;

    @Comment("管理密钥")
    private String adminKey;
    @Comment("JWT密钥")
    private String jwtKey;


}
