package wxdgaming.boot2.starter.net.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

/**
 * 基于apache的http get 请求
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-04-28 16:02
 **/
@Slf4j
public class Get extends HttpBase<Get> {

    public Get(HttpClientPool httpClientPool, String uriPath) {
        super(httpClientPool, uriPath);
    }

    @Override public void request0() throws IOException {
        HttpGet httpRequest = createGet();
        if (log.isDebugEnabled()) {
            log.info("send url={}", url());
        }

        CloseableHttpClient closeableHttpClient = httpClientPool.getCloseableHttpClient();
        closeableHttpClient.execute(httpRequest, classicHttpResponse -> {
            response.httpResponse = classicHttpResponse;
            response.cookieStore = httpClientPool.getCookieStore().getCookies();
            response.setBodys(EntityUtils.toByteArray(classicHttpResponse.getEntity()));
            return null;
        });
    }

}
