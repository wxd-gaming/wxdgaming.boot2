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
            20,300,
            30,
            3000,3000,3000,30000,
            "TLS"
    );

    @JSONField(ordinal = 1)
    private final int core;
    @JSONField(ordinal = 2)
    private final int max;
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

    @JSONCreator
    public HttpClientConfig(@JSONField(name = "core") int core,
                            @JSONField(name = "max") int max,
                            @JSONField(name = "resetTimeM") int resetTimeM,
                            @JSONField(name = "connectionRequestTimeout") int connectionRequestTimeout,
                            @JSONField(name = "connectTimeOut") int connectTimeOut,
                            @JSONField(name = "readTimeout") int readTimeout,
                            @JSONField(name = "keepAliveTimeout") int keepAliveTimeout,
                            @JSONField(name = "sslProtocol") String sslProtocol) {
        this.core = core;
        this.max = max;
        this.resetTimeM = resetTimeM;
        this.connectionRequestTimeout = connectionRequestTimeout;
        this.connectTimeOut = connectTimeOut;
        this.readTimeout = readTimeout;
        this.keepAliveTimeout = keepAliveTimeout;
        this.sslProtocol = sslProtocol;
    }
}
