package wxdgaming.boot2.starter.net.httpclient5;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import wxdgaming.boot2.core.zip.GzipUtil;
import wxdgaming.boot2.starter.net.http.HttpDataAction;

import java.util.Map;

/**
 * get请求
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-22 17:12
 **/
@Slf4j
@Getter
@SuperBuilder
public class PostRequest extends HttpRequestBase {

    public static PostRequest of(String url) {
        return PostRequest.builder().uriPath(url).build();
    }

    public static PostRequest of(String url, String params) {
        return PostRequest.builder().uriPath(url).build().setParams(params);
    }

    public static PostRequest of(String url, Map<String, ?> params) {
        return PostRequest.builder().uriPath(url).build().setParams(params);
    }

    public static PostRequest ofJson(String url, String params) {
        return PostRequest.builder().uriPath(url).build().setJson(params);
    }

    public static PostRequest ofJson(String url, Map<String, ?> params) {
        return PostRequest.builder().uriPath(url).build().setJson(params);
    }

    @Builder.Default
    private ContentType contentType = HttpConst.APPLICATION_FORM_URLENCODED;
    private String params;
    @Builder.Default
    private boolean gzip = true;

    @Override public PostRequest addHeader(HttpHeaderNames key, ContentType contentType) {
        super.addHeader(key, contentType);
        return this;
    }

    @Override public PostRequest addHeader(String key, String value) {
        super.addHeader(key, value);
        return this;
    }

    public PostRequest setParams(Map<String, ?> params) {
        return setParams(HttpDataAction.httpData(params));
    }

    public PostRequest setParamsEncoder(Map<String, ?> params) {
        return setParams(HttpDataAction.httpDataEncoder(params));
    }

    public PostRequest setParamsRawEncoder(Map<String, ?> params) {
        return setParams(HttpDataAction.httpDataRawEncoder(params));
    }

    public PostRequest setParams(String params) {
        this.params = params;
        return this;
    }

    public PostRequest setJson(Map<String, ?> params) {
        return setJson(JSON.toJSONString(params, SerializerFeature.SortField, SerializerFeature.MapSortField));
    }

    public PostRequest setJson(String params) {
        this.params = params;
        this.contentType = HttpConst.APPLICATION_JSON;
        return this;
    }

    @Override protected HttpUriRequestBase buildRequest() {
        HttpPost httpPost = new HttpPost(this.getUriPath());
        if (this.params != null) {
            byte[] bytes = params.getBytes(contentType.getCharset());
            if (gzip && bytes.length > 512) {
                // 设置请求头，告知服务器请求内容使用 Gzip 压缩
                addHeader(HttpHeaderNames.CONTENT_ENCODING.toString(), "gzip");
                bytes = GzipUtil.gzip(bytes);
            }
            ByteArrayEntity requestEntity = new ByteArrayEntity(bytes, contentType);
            httpPost.setEntity(requestEntity);
        }
        if (log.isDebugEnabled()) {
            log.debug("send post url={}, params={}", getUriPath(), params);
        }
        return httpPost;
    }

}
