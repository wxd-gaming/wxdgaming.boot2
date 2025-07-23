package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.starter.net.httpclient5.GetRequest;
import wxdgaming.boot2.starter.net.httpclient5.HttpContent;
import wxdgaming.boot2.starter.net.httpclient5.PostRequest;

public class HttpClientTest {

    @Test
    public void baidu() {
        HttpContent httpContent = GetRequest.of("https://www.baidu.com").execute();
        String string = httpContent.bodyString();
        System.out.println(string);
    }

    @Test
    public void post() {
        HttpContent httpContent = PostRequest.of("http://localhost:19800/login/check", "appId=1&token=sfo23r409283mnscoijer20389&username=admin&password=admin").execute();
        String string = httpContent.bodyString();
        System.out.println(string);
    }

    @Test
    public void postJson() {
        String jsonString = "{\"appId\":1,\"token\":\"sfo23r409283mnscoijer20389\",\"username\":\"admin\",\"password\":\"admin\"}";
        HttpContent httpContent = PostRequest.ofJson("http://localhost:19800/login/check", jsonString).execute();
        String string = httpContent.bodyString();
        System.out.println(string);
    }

}
