package wxdgaming.boot2.starter.net.httpclient5;

import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ContentType;

/**
 * get请求
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-22 17:12
 **/
@Slf4j
@Getter
public class HttpRequestGet extends AbstractHttpRequest {

    public static HttpRequestGet of(String url) {
        return new HttpRequestGet().uriPath(url).retry(0);
    }

    @Override public HttpRequestGet uriPath(String uriPath) {
        super.uriPath(uriPath);
        return this;
    }

    @Override public HttpRequestGet connectionRequestTimeout(int connectionRequestTimeout) {
        super.connectionRequestTimeout(connectionRequestTimeout);
        return this;
    }

    @Override public HttpRequestGet connectionTimeout(int connectionTimeout) {
        super.connectionTimeout(connectionTimeout);
        return this;
    }

    @Override public HttpRequestGet readTimeout(int readTimeout) {
        super.readTimeout(readTimeout);
        return this;
    }

    @Override public HttpRequestGet retry(int retry) {
        super.retry(retry);
        return this;
    }

    @Override public HttpRequestGet addHeader(HttpHeaderNames key, ContentType contentType) {
        super.addHeader(key, contentType);
        return this;
    }

    @Override public HttpRequestGet addHeader(String key, String value) {
        super.addHeader(key, value);
        return this;
    }

    @Override protected HttpUriRequestBase buildRequest() {
        if (log.isDebugEnabled()) {
            log.debug("send get url={}", uriPath());
        }
        return new HttpGet(this.uriPath());
    }

}
