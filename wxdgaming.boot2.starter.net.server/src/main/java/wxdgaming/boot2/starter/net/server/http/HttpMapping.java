package wxdgaming.boot2.starter.net.server.http;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.assist.JavassistProxy;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.starter.net.ann.HttpRequest;

import java.util.List;

/**
 * 映射关系绑定
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-18 08:50
 */
@Slf4j
public record HttpMapping(HttpRequest httpRequest, String path, JavassistProxy javassistProxy,
        List<String> pathMatcherList) {

    public String comment() {
        if (StringUtils.isBlank(httpRequest.comment())) {
            return path;
        }
        return httpRequest.comment();
    }

}
