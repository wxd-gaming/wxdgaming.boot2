package wxdgaming.minitieba;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.core.BootstrapProperties;
import wxdgaming.boot2.core.InitPrint;

/**
 * 迷你贴吧配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "boot")
public class MinitiebaProperties extends BootstrapProperties implements InitPrint {

    /** JWT密钥，用于生成和验证Token */
    private String jwtKey = "minitieba_default_jwt_key_2026";
    /** Token有效时长（小时） */
    private int tokenExpireHours = 24;

}
