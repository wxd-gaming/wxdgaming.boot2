package wxdgaming.boot2.starter.net.server.http;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;

/**
 * 配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-19 17:05
 **/
@Getter
public class HttpServerConfig {

    public static final HttpServerConfig INSTANCE = new HttpServerConfig(false, false);

    private final boolean showRequest;
    private final boolean showResponse;

    @JSONCreator
    public HttpServerConfig(@JSONField(name = "showRequest") boolean showRequest,
                            @JSONField(name = "showResponse") boolean showResponse) {
        this.showRequest = showRequest;
        this.showResponse = showResponse;
    }

}
