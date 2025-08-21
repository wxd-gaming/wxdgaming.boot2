package wxdgaming.boot2.starter.net.httpclient5;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
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
@Configuration
public class HttpClientProperties extends ObjectBase implements InitPrint {

    /** 每个路由创建的最大连接数 */
    @JSONField(ordinal = 1)
    private final int routeMaxSize;
    /** 总链接数 */
    @JSONField(ordinal = 2)
    private final int totalMaxSize;
    @JSONField(ordinal = 3)
    private final int resetTimeM;
    @JSONField(ordinal = 4)
    private final int connectionRequestTimeout;
    @JSONField(ordinal = 5)
    private final int connectTimeOut;
    @JSONField(ordinal = 6)
    private final int readTimeout;
    @JSONField(ordinal = 7)
    private final int keepAliveTimeout;
    @JSONField(ordinal = 8)
    private final String sslProtocol;
    @JSONField(ordinal = 9)
    private final boolean autoUseGzip;

    public HttpClientProperties(@Value("${http.client.routeMaxSize:500}") int routeMaxSize,
                                @Value("${http.client.totalMaxSize:5000}") int totalMaxSize,
                                @Value("${http.client.resetTimeM:30}") int resetTimeM,
                                @Value("${http.client.connectionRequestTimeout:3000}") int connectionRequestTimeout,
                                @Value("${http.client.connectTimeOut:3000}") int connectTimeOut,
                                @Value("${http.client.readTimeout:3000}") int readTimeout,
                                @Value("${http.client.keepAliveTimeout:30000}") int keepAliveTimeout,
                                @Value("${http.client.sslProtocol:TLS}") String sslProtocol,
                                @Value("${http.client.autoUseGzip:false}") boolean autoUseGzip) {
        this.routeMaxSize = routeMaxSize;
        this.totalMaxSize = totalMaxSize;
        this.resetTimeM = resetTimeM;
        this.connectionRequestTimeout = connectionRequestTimeout;
        this.connectTimeOut = connectTimeOut;
        this.readTimeout = readTimeout;
        this.keepAliveTimeout = keepAliveTimeout;
        this.sslProtocol = sslProtocol;
        this.autoUseGzip = autoUseGzip;
    }
}
