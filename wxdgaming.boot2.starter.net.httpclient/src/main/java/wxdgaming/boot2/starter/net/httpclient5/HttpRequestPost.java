package wxdgaming.boot2.starter.net.httpclient5;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.GzipCompressingEntity;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.starter.net.http.HttpDataAction;

import java.util.Map;

/**
 * get请求
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-22 17:12
 **/
@Slf4j
@Getter
@Setter
@Accessors(fluent = true)
public class HttpRequestPost extends AbstractHttpRequest {

    public static HttpRequestPost of(String url) {
        return new HttpRequestPost().uriPath(url);
    }

    public static HttpRequestPost of(String url, String params) {
        return new HttpRequestPost().uriPath(url).params(params);
    }

    public static HttpRequestPost of(String url, Map<String, ?> params) {
        return new HttpRequestPost().uriPath(url).setParams(params);
    }

    public static HttpRequestPost ofJson(String url, String params) {
        return new HttpRequestPost().uriPath(url).setParamsJson(params);
    }

    public static HttpRequestPost ofJson(String url, Map<String, ?> params) {
        return new HttpRequestPost().uriPath(url).setParamsJson(params);
    }

    private String params;
    /** 发送数据的时候是否开启gzip压缩 */
    private boolean useGzip = false;
    protected ContentType contentType = HttpConst.APPLICATION_FORM_URLENCODED;

    @Override public HttpRequestPost uriPath(String uriPath) {
        super.uriPath(uriPath);
        return this;
    }

    @Override public HttpRequestPost connectionRequestTimeout(int connectionRequestTimeout) {
        super.connectionRequestTimeout(connectionRequestTimeout);
        return this;
    }

    @Override public HttpRequestPost connectionTimeout(int connectionTimeout) {
        super.connectionTimeout(connectionTimeout);
        return this;
    }

    @Override public HttpRequestPost readTimeout(int readTimeout) {
        super.readTimeout(readTimeout);
        return this;
    }

    @Override public HttpRequestPost retry(int retry) {
        super.retry(retry);
        return this;
    }

    @Override public HttpRequestPost addHeader(HttpHeaderNames key, ContentType contentType) {
        super.addHeader(key, contentType);
        return this;
    }

    @Override public HttpRequestPost addHeader(String key, String value) {
        super.addHeader(key, value);
        return this;
    }

    public HttpRequestPost setParams(Map<String, ?> params) {
        return params(HttpDataAction.httpData(params));
    }

    /** 采用 urlencode */
    public HttpRequestPost setParamsEncoder(Map<String, ?> params) {
        return params(HttpDataAction.httpDataEncoder(params));
    }

    /** 和php一样的 urlencode */
    public HttpRequestPost setParamsRawEncoder(Map<String, ?> params) {
        return params(HttpDataAction.httpDataRawEncoder(params));
    }

    public HttpRequestPost setParamsJson(Map<String, ?> params) {
        return setParamsJson(JSON.toJSONString(params, SerializerFeature.SortField, SerializerFeature.MapSortField));
    }

    public HttpRequestPost setParamsJson(String params) {
        this.params = params;
        this.contentType = HttpConst.APPLICATION_JSON;
        return this;
    }

    public HttpRequestPost useGzip() {
        this.useGzip = true;
        return this;
    }

    @Override protected HttpUriRequestBase buildRequest() {
        HttpPost httpPost = new HttpPost(this.uriPath());
        if (this.params != null) {
            HttpEntity httpEntity = new StringEntity(params, contentType);
            if (useGzip && httpEntity.getContentLength() > Const.USE_GZIP_MIN_LENGTH) {
                httpEntity = new GzipCompressingEntity(httpEntity);
            }
            httpPost.setEntity(httpEntity);
        }
        return httpPost;
    }

}
