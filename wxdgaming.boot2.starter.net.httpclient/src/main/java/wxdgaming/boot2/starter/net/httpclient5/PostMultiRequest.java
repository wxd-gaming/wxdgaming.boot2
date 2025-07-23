package wxdgaming.boot2.starter.net.httpclient5;

import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import wxdgaming.boot2.starter.net.http.HttpDataAction;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 多段式
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-23 16:35
 **/
@Slf4j
@Getter
@SuperBuilder
public class PostMultiRequest extends HttpRequestBase {

    public static PostRequest of(String url) {
        return PostRequest.builder().uriPath(url).build();
    }

    private final ContentType contentType = HttpConst.MULTIPART_FORM_DATA;
    private final HashMap<String, Object> objMap = new HashMap<>();

    @Override public PostMultiRequest addHeader(HttpHeaderNames key, ContentType contentType) {
        super.addHeader(key, contentType);
        return this;
    }

    @Override public PostMultiRequest addHeader(String key, String value) {
        super.addHeader(key, value);
        return this;
    }

    public PostMultiRequest addParam(String key, Object value) {
        objMap.put(key, value);
        return this;
    }

    public PostMultiRequest setParamsEncoder(String key, Object value) {
        return addParam(key, HttpDataAction.urlEncoder(value));
    }

    public PostMultiRequest setParamsRawEncoder(String key, Object value) {
        return addParam(key, HttpDataAction.rawUrlEncode(value));
    }

    @Override protected HttpUriRequestBase buildRequest() {
        HttpPost httpRequest = new HttpPost(this.getUriPath());
        if (!objMap.isEmpty()) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setContentType(contentType);
            for (Map.Entry<String, Object> entry : objMap.entrySet()) {
                String key = entry.getKey();
                if (entry.getValue() instanceof File file) {
                    builder.addBinaryBody(key, file);
                    builder.addTextBody(file.getName() + "_lastModified", file.lastModified() + "");
                } else if (entry.getValue() instanceof byte[] bytes) {
                    builder.addBinaryBody(key, bytes);
                } else {
                    builder.addTextBody(key, String.valueOf(entry.getValue()));
                }
            }
            HttpEntity build = builder.build();
            httpRequest.setEntity(build);
            if (log.isDebugEnabled()) {
                try {
                    String s = EntityUtils.toString(build);
                    log.debug("send post multi url={}\n{}", getUriPath(), s);
                } catch (Exception e) {
                    log.debug("send post multi url={}", getUriPath(), e);
                }
            }
        }
        return httpRequest;
    }
}
