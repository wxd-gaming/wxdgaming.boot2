package wxdgaming.boot2.starter.net.httpclient;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 10:26
 **/
@Getter
@Setter
public class HttpClientConfig extends ObjectBase {

    @JSONField(ordinal = 1)
    private int core = 20;
    @JSONField(ordinal = 2)
    private int max = 300;
    @JSONField(ordinal = 3)
    private int resetTimeM = 30;
    @JSONField(ordinal = 4)
    private int connectionRequestTimeout = 1000;
    @JSONField(ordinal = 5)
    private int connectTimeOut = 3000;
    @JSONField(ordinal = 6)
    private int readTimeout = 3000;
    @JSONField(ordinal = 7)
    private int keepAliveTimeout = 30000;
    @JSONField(ordinal = 8)
    private String sslProtocol = "TLS";

}
