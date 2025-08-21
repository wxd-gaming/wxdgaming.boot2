package wxdgaming.boot2.starter.net.httpclient5;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-23 13:16
 **/
@Slf4j
@Getter
@Setter
@Accessors(fluent = true)
public abstract class AbstractHttpRequest {

    protected String uriPath;
    /** 重试次数，最小值是1 */
    protected int retry;
    protected final Map<String, String> reqHeaderMap = new LinkedHashMap<>();

    protected abstract HttpUriRequestBase buildRequest();

    public HttpResponse execute() {
        retry++;
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
                HttpClientConfiguration httpClientConfiguration = HttpClientConfiguration.getInstance();
                CloseableHttpClient closeableHttpClient = httpClientConfiguration.getCloseableHttpClient();
                return closeableHttpClient.execute(httpUriRequestBase, classicHttpResponse -> {
                    /*apache http client 已经自动处理过 gzip 问题*/
                    HttpResponse httpResponse = new HttpResponse();
                    httpResponse.classicHttpResponse = classicHttpResponse;
                    httpResponse.code = classicHttpResponse.getCode();
                    httpResponse.cookieStore = httpClientConfiguration.getCookieStore().getCookies();
                    httpResponse.content = EntityUtils.toByteArray(classicHttpResponse.getEntity());
                    return httpResponse;
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
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.code = 500;
        httpResponse.exception = Throw.of(exception);
        return httpResponse;
    }

    public AbstractHttpRequest addHeader(HttpHeaderNames key, ContentType contentType) {
        reqHeaderMap.put(key.toString(), contentType.toString());
        return this;
    }

    public AbstractHttpRequest addHeader(String key, String value) {
        reqHeaderMap.put(key, value);
        return this;
    }

}
