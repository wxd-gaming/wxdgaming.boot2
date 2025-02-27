package wxdgaming.boot2.starter.net.httpclient;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import wxdgaming.boot2.starter.net.http.HttpDataAction;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class PostMulti extends HttpBase<PostMulti> {

    private ContentType contentType = ContentType.MULTIPART_FORM_DATA;
    private HashMap<Object, Object> objMap = new HashMap<>();

    public PostMulti(HttpClientPool httpClientPool, String uriPath) {
        super(httpClientPool, uriPath);
    }

    @Override public void request0() throws IOException {
        HttpPost httpRequest = createPost();
        if (!objMap.isEmpty()) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setContentType(contentType);
            for (Map.Entry<Object, Object> objectObjectEntry : objMap.entrySet()) {
                String key = String.valueOf(objectObjectEntry.getKey());
                if (objectObjectEntry.getValue() instanceof File file) {
                    builder.addBinaryBody(key, file);
                    builder.addTextBody(file.getName() + "_lastModified", file.lastModified() + "");
                } else if (objectObjectEntry.getValue() instanceof byte[] bytes) {
                    builder.addBinaryBody(key, bytes);
                } else {
                    builder.addTextBody(key, String.valueOf(objectObjectEntry.getValue()));
                }
            }
            org.apache.hc.core5.http.HttpEntity build = builder.build();
            httpRequest.setEntity(build);
            if (log.isDebugEnabled()) {
                String s = new String(readBytes(build));
                log.info("send url={}\n{}", url(), s);
            }
        }

        CloseableHttpClient closeableHttpClient = httpClientPool.getCloseableHttpClient();
        closeableHttpClient.execute(httpRequest, classicHttpResponse -> {
            response.httpResponse = classicHttpResponse;
            response.cookieStore = httpClientPool.getCookieStore().getCookies();
            response.bodys = EntityUtils.toByteArray(classicHttpResponse.getEntity());
            return null;
        });
    }

    @Override public PostMulti addHeader(String headerKey, String HeaderValue) {
        super.addHeader(headerKey, HeaderValue);
        return this;
    }

    public PostMulti addParams(Object name, Object value) {
        return addParams(name, value, true);
    }

    public PostMulti addParams(Object name, Object value, boolean urlEncode) {
        if (urlEncode) {
            objMap.put(name, HttpDataAction.urlDecoder(String.valueOf(value)));
        } else {
            objMap.put(name, String.valueOf(value));
        }
        return this;
    }

    public PostMulti addParams(Map<?, ?> map, boolean urlEncode) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            addParams(entry.getKey(), entry.getValue(), urlEncode);
        }
        return this;
    }

}
