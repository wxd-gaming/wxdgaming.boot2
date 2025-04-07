package wxdgaming.boot2.starter.net.server.http;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.assist.JavassistInvoke;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.starter.net.ann.HttpRequest;

/**
 * 映射关系绑定
 *
 * @param httpRequest      请求信息
 * @param path             请求路径
 * @param javassistInvoke 代理对象
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 08:50
 */
@Slf4j
public record HttpMapping(HttpRequest httpRequest, String path, JavassistInvoke javassistInvoke) {

    public String comment() {
        if (StringUtils.isBlank(httpRequest.comment())) {
            return path;
        }
        return httpRequest.comment();
    }

}
