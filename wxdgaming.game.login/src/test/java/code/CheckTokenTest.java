package code;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientScan;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;

@SpringBootTest(classes={HttpClientScan.class})
public class CheckTokenTest {

    @Test
    public void login() {

        String url = "http://127.0.0.1:19800/login/check";
        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("appId", 1);
        jsonObject.put("platform", 1);
        jsonObject.put("account", "abc");
        jsonObject.put("token", "abc");

        HttpResponse execute = HttpRequestPost.of(url, jsonObject).execute();
        System.out.println(jsonObject);
        System.out.println(execute);

    }

}
