package wxdgaming.boot2.starter.net.server.http;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.function.Supplier;

/**
 * 配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-19 17:05
 **/
@Getter
public class HttpServerConfig extends ObjectBase {

    public static final Supplier<Object> INSTANCE = () -> new HttpServerConfig(false, false, 30);

    private final boolean showRequest;
    private final boolean showResponse;
    /** 如果不需要缓存设置-1 */
    private final int experienceSeconds;

    @JSONCreator
    public HttpServerConfig(@JSONField(name = "showRequest") boolean showRequest,
                            @JSONField(name = "showResponse") boolean showResponse,
                            @JSONField(name = "experienceSeconds") int experienceSeconds) {
        this.showRequest = showRequest;
        this.showResponse = showResponse;
        this.experienceSeconds = experienceSeconds;
    }

    public int getExperienceSeconds() {
        if (experienceSeconds == 0) {
            return 30;
        }
        return experienceSeconds;
    }
}
