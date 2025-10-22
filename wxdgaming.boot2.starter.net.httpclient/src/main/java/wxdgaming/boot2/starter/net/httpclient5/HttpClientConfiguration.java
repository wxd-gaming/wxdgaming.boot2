package wxdgaming.boot2.starter.net.httpclient5;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.core.InitPrint;

import java.util.concurrent.TimeUnit;

/**
 * 基于apache的http 连接池
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2023-04-28 12:30
 **/
@Slf4j
@Getter
@Configuration
public class HttpClientConfiguration implements InitPrint {

    public static class Lazy {
        private static HttpClientConfiguration instance;
    }

    public static HttpClientConfiguration getInstance() {
        return Lazy.instance;
    }

    private final HttpClientProperties clientConfig;
    protected PoolingHttpClientConnectionManager connPoolMng;
    protected CloseableHttpClient closeableHttpClient;
    // 创建一个 Cookie 存储对象
    protected final CookieStore cookieStore = new BasicCookieStore();

    public HttpClientConfiguration(HttpClientProperties clientConfig) {
        this.clientConfig = clientConfig;
        this.build();
        Lazy.instance = this;
    }

    public void stop() {
        try {
            if (this.connPoolMng != null) {
                this.connPoolMng.close();
            }
        } catch (Exception ignore) {}
        try {
            if (this.closeableHttpClient != null) {
                this.closeableHttpClient.close();
            }
        } catch (Exception ignore) {}
    }

    public void build() {
        try {
            // 初始化http连接池
            connPoolMng = new PoolingHttpClientConnectionManager();
            // 设置总的连接数为200，每个路由的最大连接数为20
            connPoolMng.setMaxTotal(clientConfig.getTotalMaxSize());
            connPoolMng.setDefaultMaxPerRoute(clientConfig.getRouteMaxSize());
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setConnectTimeout(clientConfig.getConnectTimeOut(), TimeUnit.MILLISECONDS)
                    .setSocketTimeout(clientConfig.getConnectTimeOut(), TimeUnit.MILLISECONDS)
                    .build();
            connPoolMng.setDefaultConnectionConfig(connectionConfig);

            // 初始化请求超时控制参数
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(clientConfig.getConnectionRequestTimeout(), TimeUnit.MILLISECONDS) // 从线程池中获取线程超时时间
                    .setResponseTimeout(clientConfig.getReadTimeout(), TimeUnit.MILLISECONDS) // 设置数据超时时间
                    .build();

            ConnectionKeepAliveStrategy connectionKeepAliveStrategy = (httpResponse, httpContext) -> {
                return TimeValue.of(clientConfig.getKeepAliveTimeout(), TimeUnit.MILLISECONDS); /*tomcat默认keepAliveTimeout为20s*/
            };


            HttpClientBuilder httpClientBuilder = HttpClients.custom()
                    .setConnectionManager(connPoolMng)
                    .setDefaultCookieStore(cookieStore)
                    //                        .evictExpiredConnections()/*关闭异常链接*/
                    //                        .evictIdleConnections(10, TimeUnit.SECONDS)/*关闭空闲链接*/
                    .setDefaultRequestConfig(requestConfig)
                    .setRetryStrategy(DefaultHttpRequestRetryStrategy.INSTANCE) /*创建一个不进行重试的策略*/
                    .setKeepAliveStrategy(connectionKeepAliveStrategy);

            closeableHttpClient = httpClientBuilder.build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
