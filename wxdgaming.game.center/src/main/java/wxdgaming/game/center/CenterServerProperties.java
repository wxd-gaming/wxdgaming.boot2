package wxdgaming.game.center;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.core.BootstrapProperties;
import wxdgaming.boot2.core.ann.Comment;

/**
 * 配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-15 09:56
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "boot")
public class CenterServerProperties extends BootstrapProperties {

    @Comment("管理员账号")
    private String adminName;
    @Comment("管理员密码")
    private String adminPwd;

    @Comment("管理密钥")
    private String adminKey;
    @Comment("JWT密钥")
    private String jwtKey;


}
