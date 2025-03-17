package wxdgaming.boot2.starter.net.httpclient;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import wxdgaming.boot2.core.zip.GzipUtil;
import wxdgaming.boot2.starter.net.http.HttpDataAction;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class PostText extends HttpBase<PostText> {

    private ContentType contentType = ContentType.APPLICATION_FORM_URLENCODED;
    private String params = "";

    public PostText(HttpClientPool httpClientPool, String uriPath) {
        super(httpClientPool, uriPath);
    }

    @Override public void request0() throws IOException {
        HttpPost httpRequest = createPost();
        if (null != params) {
            byte[] bytes = params.getBytes(StandardCharsets.UTF_8);
            if (bytes.length > 512) {
                // 设置请求头，告知服务器请求内容使用 Gzip 压缩
                httpRequest.setHeader("Content-Encoding", "gzip");
                bytes = GzipUtil.gzip(bytes);
            }
            ByteArrayEntity requestEntity = new ByteArrayEntity(bytes, contentType);
            httpRequest.setEntity(requestEntity);
            if (log.isDebugEnabled()) {
                log.debug("send url={}\n{}", url(), params);
            }
        }
        CloseableHttpClient closeableHttpClient = httpClientPool.getCloseableHttpClient();
        closeableHttpClient.execute(httpRequest, classicHttpResponse -> {
            response.httpResponse = classicHttpResponse;
            response.cookieStore = httpClientPool.getCookieStore().getCookies();
            response.setBodys(EntityUtils.toByteArray(classicHttpResponse.getEntity()));
            return null;
        });
    }

    @Override public PostText addHeader(String headerKey, String HeaderValue) {
        super.addHeader(headerKey, HeaderValue);
        return this;
    }

    public PostText addParams(Object name, Object value) {
        return addParams(name, value, true);
    }

    public PostText addParams(Object name, Object value, boolean urlEncode) {
        if (!this.params.isEmpty()) {
            this.params += "&";
        }
        this.params += String.valueOf(name) + "=";
        if (urlEncode) {
            this.params += URLDecoder.decode(String.valueOf(value), StandardCharsets.UTF_8);
        } else {
            this.params += String.valueOf(value);
        }
        return this;
    }

    public PostText setParams(String params) {
        this.params = params;
        return this;
    }

    public PostText setParams(Map<?, ?> map) {
        return setParams(map, true);
    }

    public PostText setParams(Map<?, ?> map, boolean urlEncode) {
        if (urlEncode) {
            this.params = HttpDataAction.httpDataEncoder(map);
        } else {
            this.params = HttpDataAction.httpData(map);
        }
        return this;
    }

    public PostText setParamsJson(Map<?, ?> map) {
        return setParamsJson(JSON.toJSONString(map));
    }

    public PostText setParamsJson(String params) {
        return setParams(ContentType.APPLICATION_JSON, params);
    }

    public PostText setParams(ContentType contentType, String params) {
        this.contentType = contentType;
        this.params = params;
        return this;
    }
}
