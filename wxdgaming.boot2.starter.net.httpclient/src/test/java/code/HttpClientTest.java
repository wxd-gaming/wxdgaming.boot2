package code;

import org.junit.Test;
import wxdgaming.boot2.core.threading.ExecutorUtilImpl;
import wxdgaming.boot2.starter.net.httpclient.Get;
import wxdgaming.boot2.starter.net.httpclient.HttpBuilder;

public class HttpClientTest {

    static {
        ExecutorUtilImpl.getInstance().init();
    }

    @Test
    public void baidu() {
        Get get = HttpBuilder.get("https://www.baidu.com");
        String string = get.request().bodyString();
        System.out.println(string);
    }

}
