package wxdgaming.boot2.starter.net.httpclient5;

import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ContentType;

/**
 * get请求
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-22 17:12
 **/
@Slf4j
@Getter
@SuperBuilder
public class GetRequest extends HttpRequestBase {

    public static GetRequest of(String url) {
        return GetRequest.builder().uriPath(url).build();
    }

    @Override public GetRequest addHeader(HttpHeaderNames key, ContentType contentType) {
        super.addHeader(key, contentType);
        return this;
    }

    @Override public GetRequest addHeader(String key, String value) {
        super.addHeader(key, value);
        return this;
    }

    @Override protected HttpUriRequestBase buildRequest() {
        if (log.isDebugEnabled()) {
            log.debug("send get url={}", getUriPath());
        }
        return new HttpGet(this.getUriPath());
    }

}
