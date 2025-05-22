package wxdgaming.boot2.starter.net.httpclient;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 10:26
 **/
@Getter
public class HttpClientConfig extends ObjectBase {

    public static final HttpClientConfig DEFAULT = new HttpClientConfig(
            500, 5000,
            30,
            3000, 3000, 3000, 30000,
            "TLS",
            true
    );

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

    @JSONCreator
    public HttpClientConfig(@JSONField(name = "routeMaxSize") int routeMaxSize,
                            @JSONField(name = "totalMaxSize") int totalMaxSize,
                            @JSONField(name = "resetTimeM") int resetTimeM,
                            @JSONField(name = "connectionRequestTimeout") int connectionRequestTimeout,
                            @JSONField(name = "connectTimeOut") int connectTimeOut,
                            @JSONField(name = "readTimeout") int readTimeout,
                            @JSONField(name = "keepAliveTimeout") int keepAliveTimeout,
                            @JSONField(name = "sslProtocol", defaultValue = "TLS") String sslProtocol,
                            @JSONField(name = "autoUseGzip", defaultValue = "false") boolean autoUseGzip) {
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
