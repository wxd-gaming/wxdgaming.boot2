package wxdgaming.boot2.starter.net.httpclient5;

import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.GzipCompressingEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.net.http.HttpDataAction;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 多段式
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-23 16:35
 **/
@Slf4j
@Getter
@Setter
@Accessors(fluent = true)
public class HttpRequestPostMulti extends AbstractHttpRequest {

    public static HttpRequestPostMulti of(String url) {
        return new HttpRequestPostMulti().uriPath(url);
    }

    private final MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
    /** 发送数据的时候是否开启gzip压缩 */
    private boolean useGzip = false;

    public HttpRequestPostMulti() {
        multipartEntityBuilder.setContentType(HttpConst.MULTIPART_FORM_DATA);
        multipartEntityBuilder.setCharset(StandardCharsets.UTF_8);
    }


    @Override public HttpRequestPostMulti uriPath(String uriPath) {
        super.uriPath(uriPath);
        return this;
    }

    @Override public HttpRequestPostMulti connectionRequestTimeout(int connectionRequestTimeout) {
        super.connectionRequestTimeout(connectionRequestTimeout);
        return this;
    }

    @Override public HttpRequestPostMulti connectionTimeout(int connectionTimeout) {
        super.connectionTimeout(connectionTimeout);
        return this;
    }

    @Override public HttpRequestPostMulti readTimeout(int readTimeout) {
        super.readTimeout(readTimeout);
        return this;
    }

    @Override public HttpRequestPostMulti retry(int retry) {
        super.retry(retry);
        return this;
    }

    @Override public HttpRequestPostMulti addHeader(HttpHeaderNames key, ContentType contentType) {
        super.addHeader(key, contentType);
        return this;
    }

    @Override public HttpRequestPostMulti addHeader(String key, String value) {
        super.addHeader(key, value);
        return this;
    }

    public HttpRequestPostMulti useGzip() {
        this.useGzip = true;
        return this;
    }

    public HttpRequestPostMulti setParam(String key, Object value) {
        if (value instanceof File file) {
            AssertUtil.isTrue(file.exists(), "文件不存在：%s", file);
            multipartEntityBuilder.addBinaryBody(key, file);
            multipartEntityBuilder.addTextBody(file.getName() + "_lastModified", file.lastModified() + "");
        } else if (value instanceof byte[] bytes) {
            multipartEntityBuilder.addBinaryBody(key, bytes);
        } else {
            multipartEntityBuilder.addTextBody(key, String.valueOf(value));
        }
        return this;
    }

    public HttpRequestPostMulti setParams(Map<String, ?> params) {
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            setParam(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /** urlencode */
    public HttpRequestPostMulti setParamsEncoder(Map<String, ?> params) {
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            setParamEncoder(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /** urlencode */
    public HttpRequestPostMulti setParamEncoder(String key, Object value) {
        return setParam(key, HttpDataAction.urlEncoder(value));
    }

    /** 和php一样的 urlencode */
    public HttpRequestPostMulti setParamsRawEncoder(Map<String, ?> params) {
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            setParamRawEncoder(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /** 和php一样的 urlencode */
    public HttpRequestPostMulti setParamRawEncoder(String key, Object value) {
        return setParam(key, HttpDataAction.rawUrlEncode(value));
    }

    @Override protected HttpUriRequestBase buildRequest() {
        HttpPost httpRequest = new HttpPost(this.uriPath());
        HttpEntity httpEntity = multipartEntityBuilder.build();
        if (useGzip && httpEntity.getContentLength() > Const.USE_GZIP_MIN_LENGTH) {
            httpEntity = new GzipCompressingEntity(httpEntity);
        }
        httpRequest.setEntity(httpEntity);
        return httpRequest;
    }
}
