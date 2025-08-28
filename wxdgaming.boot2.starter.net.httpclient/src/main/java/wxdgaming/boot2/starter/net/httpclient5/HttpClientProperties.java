package wxdgaming.boot2.starter.net.httpclient5;

import com.alibaba.fastjson.annotation.JSONField;
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
 * @version 2025-02-18 10:26
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "http.client")
public class HttpClientProperties extends ObjectBase implements InitPrint {

    /** 每个路由创建的最大连接数 */
    @JSONField(ordinal = 1)
    private int routeMaxSize = 500;
    /** 总链接数 */
    @JSONField(ordinal = 2)
    private int totalMaxSize = 5000;
    @JSONField(ordinal = 3)
    private int resetTimeM = 30;
    @JSONField(ordinal = 4)
    private int connectionRequestTimeout = 1000;
    @JSONField(ordinal = 5)
    private int connectTimeOut = 2000;
    @JSONField(ordinal = 6)
    private int readTimeout = 3000;
    @JSONField(ordinal = 7)
    private int keepAliveTimeout = 30000;
    @JSONField(ordinal = 8)
    private String sslProtocol = "TLS";
    @JSONField(ordinal = 9)
    private boolean autoUseGzip = false;


}
