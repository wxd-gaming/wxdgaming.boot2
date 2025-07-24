package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestGet;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPostMulti;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;

import java.util.Map;

public class HttpClientTest {

    @Test
    public void baidu() {
        HttpResponse httpResponse = HttpRequestGet.of("https://www.baidu.com").execute();
        String string = httpResponse.bodyString();
        System.out.println(string);
    }

    @Test
    public void post() {
        HttpResponse httpResponse = HttpRequestPost.of("http://localhost:19800/login/check", "appId=1&token=sfo23r409283mnscoijer20389&username=admin&password=admin").execute();
        String string = httpResponse.bodyString();
        System.out.println(string);
    }
    @Test
    public void postFormData() {
        Map<String, String> jsonString = Map.of("appId", "1", "token", "sfo23r409283mnscoijer20389", "username", "admin", "password", "admin");
        HttpResponse httpResponse = HttpRequestPost.of("http://localhost:19800/login/test/1/v1", jsonString).execute();
        String string = httpResponse.bodyString();
        System.out.println(string);
    }
    @Test
    public void postJson() {
        Map<String, String> jsonString = Map.of("appId", "1", "token", "sfo23r409283mnscoijer20389", "username", "admin", "password", "admin");
        HttpResponse httpResponse = HttpRequestPost.ofJson("http://localhost:19800/login/test/1/v1", jsonString).execute();
        String string = httpResponse.bodyString();
        System.out.println(string);
    }

    @Test
    public void postGzip() {
        String jsonString = """
                {"appId":1,"token":"%s","account":"admin","password":"admin","test":"我收到"}
                """.formatted("sfo23r409283mnscoijer20389".repeat(1000));
        HttpResponse httpResponse = HttpRequestPost.of("http://localhost:19800/login/test/1/v1", jsonString).useGzip().execute();
        String string = httpResponse.bodyString();
        System.out.println(string);
    }

    @Test
    public void postMultiGzip() {
        HttpRequestPostMulti httpRequestPost = HttpRequestPostMulti.of("http://localhost:19800/login/test/1/v1");
        httpRequestPost.addParam("appId", 1);
        httpRequestPost.addParam("token", "sfo23r409283mnscoijer20389".repeat(10000));
        httpRequestPost.addParam("account", "我收到");

        HttpResponse httpResponse = httpRequestPost.useGzip().execute();
        String string = httpResponse.bodyString();
        System.out.println(string);
    }

}
