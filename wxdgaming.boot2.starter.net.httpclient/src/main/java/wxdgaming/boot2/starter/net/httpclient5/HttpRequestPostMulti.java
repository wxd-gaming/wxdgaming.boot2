package wxdgaming.boot2.starter.net.httpclient5;

import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.GzipCompressingEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.net.http.HttpDataAction;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 多段式
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-23 16:35
 **/
@Slf4j
@Getter
@SuperBuilder
public class HttpRequestPostMulti extends AbstractHttpRequest {

    public static HttpRequestPostMulti of(String url) {
        return HttpRequestPostMulti.builder().uriPath(url).build();
    }

    private final HashMap<String, Object> objMap = new HashMap<>();
    /** 发送数据的时候是否开启gzip压缩 */
    @Builder.Default
    private boolean useGzip = false;

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

    public HttpRequestPostMulti addParam(String key, Object value) {
        objMap.put(key, value);
        return this;
    }

    public HttpRequestPostMulti setParamsEncoder(String key, Object value) {
        return addParam(key, HttpDataAction.urlEncoder(value));
    }

    public HttpRequestPostMulti setParamsRawEncoder(String key, Object value) {
        return addParam(key, HttpDataAction.rawUrlEncode(value));
    }

    @Override protected HttpUriRequestBase buildRequest() {
        HttpPost httpRequest = new HttpPost(this.getUriPath());
        if (!objMap.isEmpty()) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setContentType(HttpConst.MULTIPART_FORM_DATA);
            builder.setCharset(StandardCharsets.UTF_8);
            for (Map.Entry<String, Object> entry : objMap.entrySet()) {
                String key = entry.getKey();
                if (entry.getValue() instanceof File file) {
                    AssertUtil.assertTrue(file.exists(), "文件不存在：%s", file);
                    builder.addBinaryBody(key, file);
                    builder.addTextBody(file.getName() + "_lastModified", file.lastModified() + "");
                } else if (entry.getValue() instanceof byte[] bytes) {
                    builder.addBinaryBody(key, bytes);
                } else {
                    builder.addTextBody(key, String.valueOf(entry.getValue()));
                }
            }
            HttpEntity httpEntity = builder.build();
            if (useGzip && httpEntity.getContentLength() > HttpDataAction.USE_GZIP_MIN_LENGTH) {
                httpEntity = new GzipCompressingEntity(httpEntity);
            }
            httpRequest.setEntity(httpEntity);
            if (log.isDebugEnabled()) {
                log.debug("send post multi url={}", getUriPath());
            }
        }
        return httpRequest;
    }
}
