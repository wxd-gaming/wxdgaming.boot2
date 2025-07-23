package wxdgaming.boot2.starter.net.httpclient5;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.util.AssertUtil;

import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 请求
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-23 13:16
 **/
@Slf4j
@Getter
@SuperBuilder
public abstract class HttpRequestBase {

    protected String uriPath;
    /** 重试次数，最小值是1 */
    @Builder.Default
    protected int retry = 1;
    @Builder.Default
    protected final Map<String, String> reqHeaderMap = new LinkedHashMap<>();

    protected abstract HttpUriRequestBase buildRequest();

    public HttpContent execute() {
        AssertUtil.assertNull(uriPath, "uriPath不能为空");
        AssertUtil.assertTrue(retry > 0, "重试次数不能小于1");
        HttpUriRequestBase httpUriRequestBase = buildRequest();

        for (Map.Entry<String, String> entry : reqHeaderMap.entrySet()) {
            httpUriRequestBase.setHeader(entry.getKey(), entry.getValue());
        }

        /*告诉服务器我支持gzip*/
        httpUriRequestBase.setHeader(HttpHeaderNames.ACCEPT_ENCODING.toString(), HttpHeaderValues.GZIP.toString());

        // 防止被当成攻击添加的
        httpUriRequestBase.setHeader(HttpHeaderNames.USER_AGENT.toString(), "Mozilla/5.0 (Windows NT 6.2; Win64; x64) wxd");

        Exception exception = null;
        for (int k = 0; k < retry; k++) {
            try {
                HttpClientPool httpClientPool = HttpClientPool.getDefault();
                CloseableHttpClient closeableHttpClient = httpClientPool.getCloseableHttpClient();
                return closeableHttpClient.execute(httpUriRequestBase, classicHttpResponse -> {
                    HttpContent httpContent = new HttpContent();
                    httpContent.classicHttpResponse = classicHttpResponse;
                    httpContent.code = classicHttpResponse.getCode();
                    httpContent.cookieStore = httpClientPool.getCookieStore().getCookies();
                    httpContent.content = EntityUtils.toByteArray(classicHttpResponse.getEntity());
                    return httpContent;
                });
            } catch (NoHttpResponseException
                     | SocketTimeoutException
                     | HttpHostConnectException e) {
                exception = e;
                if (k > 0) {
                    log.error("请求异常，重试 {}", k, e);
                }
            } catch (IllegalStateException | InterruptedIOException e) {
                exception = e;
                /*todo 因为意外链接终止了 重新构建 */
                String string = e.toString();
                if (string.contains("shut") && string.contains("down")) {
                    log.error("连接池可能意外关闭了重新构建，等待重试 {} {}", k, string);
                } else {
                    log.error("连接池可能意外关闭了重新构建，等待重试 {}", k, e);
                }
            } catch (Exception e) {
                exception = e;
            }
        }
        HttpContent httpContent = new HttpContent();
        httpContent.code = 500;
        httpContent.exception = Throw.of(exception);
        return httpContent;
    }

    public HttpRequestBase addHeader(HttpHeaderNames key, ContentType contentType) {
        reqHeaderMap.put(key.toString(), contentType.toString());
        return this;
    }

    public HttpRequestBase addHeader(String key, String value) {
        reqHeaderMap.put(key, value);
        return this;
    }

}
