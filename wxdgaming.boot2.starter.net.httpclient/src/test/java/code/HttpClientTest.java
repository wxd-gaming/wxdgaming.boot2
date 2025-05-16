package code;

import org.junit.Test;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.starter.net.httpclient.Get;
import wxdgaming.boot2.starter.net.httpclient.HttpBuilder;

public class HttpClientTest {

    @Test
    public void baidu() {
        Get get = HttpBuilder.get("https://www.baidu.com");
        String string = get.request().bodyString();
        System.out.println(string);
    }

}
