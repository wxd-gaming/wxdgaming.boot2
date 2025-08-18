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
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.cache2.CASCache;
import wxdgaming.boot2.core.cache2.Cache;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.function.Function1;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于apache的http 连接池
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2023-04-28 12:30
 **/
@Slf4j
@Getter
public class HttpClientPool {

    public static final ReentrantLock lock = new ReentrantLock();
    protected static final Cache<String, HttpClientPool> HTTP_CLIENT_CACHE;

    static {
        HttpClientConfig clientConfig = BootConfig.getIns().getNestedValue("http.client", HttpClientConfig.class, HttpClientConfig.DEFAULT);
        log.debug("HttpClientConfig: {}", clientConfig);
        HTTP_CLIENT_CACHE = CASCache.<String, HttpClientPool>builder()
                .cacheName("http-client")
                .expireAfterWriteMs(TimeUnit.MINUTES.toMillis(clientConfig.getResetTimeM()))
                .loader((Function1<String, HttpClientPool>) s -> build(clientConfig))
                .removalListener((k, pool) -> {
                    ExecutorFactory.getExecutorServiceLogic().schedule(
                            pool::shutdown,
                            30,
                            TimeUnit.SECONDS
                    );
                    return true;
                })
                .build();
        HTTP_CLIENT_CACHE.start();
    }

    public static HttpClientPool getDefault() {
        return getDefault("0-0");
    }

    public static HttpClientPool getDefault(String key) {
        return HTTP_CLIENT_CACHE.get(key);
    }

    public static HttpClientPool build(HttpClientConfig clientConfig) {
        return new HttpClientPool(clientConfig);
    }

    private final HttpClientConfig clientConfig;
    protected PoolingHttpClientConnectionManager connPoolMng;
    protected CloseableHttpClient closeableHttpClient;
    // 创建一个 Cookie 存储对象
    protected final CookieStore cookieStore = new BasicCookieStore();

    public HttpClientPool(HttpClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.build();
    }

    public CloseableHttpClient getCloseableHttpClient() {
        lock.lock();
        try {
            return closeableHttpClient;
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
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
        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }
    }

}
