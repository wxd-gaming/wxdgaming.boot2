package wxdgaming.boot2.starter.net.httpclient;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
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
        HttpPost httpRequestBase = createPost();
        if (null != params) {
            StringEntity stringEntity = new StringEntity(params, contentType);
            httpRequestBase.setEntity(stringEntity);
            if (log.isDebugEnabled()) {
                String s = new String(readBytes(stringEntity));
                log.info("send url={}\n{}", url(), s);
            }
        }
        response.httpResponse = httpClientPool.getCloseableHttpClient().execute(httpRequestBase);
        response.cookieStore = httpClientPool.getCookieStore().getCookies();
        HttpEntity entity = response.httpResponse.getEntity();
        response.bodys = EntityUtils.toByteArray(entity);
        EntityUtils.consume(entity);
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
