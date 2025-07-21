package wxdgaming.boot2.starter.net.httpclient;


import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.reflect.ReflectProvider;

/**
 * socket 模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 09:45
 **/
public class HttpClientGuiceModule extends ServiceGuiceModule {


    public HttpClientGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }

    @Override protected void bind() throws Throwable {
        HttpClientPool aDefault = HttpClientPool.getDefault();
    }

}
