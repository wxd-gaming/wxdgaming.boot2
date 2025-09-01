package wxdgaming.game.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientScan;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;

@Slf4j
@SpringBootTest(classes = {
        CoreScan.class,
        HttpClientScan.class,
})
@EnableConfigurationProperties(LoginServerProperties.class)
public class CDkeyTest {

    @Autowired LoginServerProperties loginServerProperties;


    @Test
    public void gain() {
        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("cdKeyId", 2);
        jsonObject.put("num", 100);

        String jsonString = JSON.toJSONString(jsonObject, SerializerFeature.MapSortField, SerializerFeature.SortField);
        String string = Md5Util.md5DigestEncode0("#", jsonString, loginServerProperties.getJwtKey());
        jsonObject.put("sign", string);
        HttpResponse execute = HttpRequestPost
                .ofJson("http://127.0.0.1:19800/inner/gainCDKey", jsonObject)
                .execute();
        log.info("生成CDKey: {}", execute.bodyString());
    }

    @Test
    public void query1() {
        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("cdKeyId", 1);

        String jsonString = JSON.toJSONString(jsonObject, SerializerFeature.MapSortField, SerializerFeature.SortField);
        String string = Md5Util.md5DigestEncode0("#", jsonString, loginServerProperties.getJwtKey());
        jsonObject.put("sign", string);
        HttpResponse execute = HttpRequestPost
                .ofJson("http://127.0.0.1:19800/inner/queryCDKey", jsonObject)
                .execute();
        log.info("查询CDKey: {}", execute.bodyString());
    }

    @Test
    public void query2() {
        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("cdKeyId", 2);

        String jsonString = JSON.toJSONString(jsonObject, SerializerFeature.MapSortField, SerializerFeature.SortField);
        String string = Md5Util.md5DigestEncode0("#", jsonString, loginServerProperties.getJwtKey());
        jsonObject.put("sign", string);
        HttpResponse execute = HttpRequestPost
                .ofJson("http://127.0.0.1:19800/inner/queryCDKey", jsonObject)
                .execute();
        log.info("查询CDKey: {}", execute.bodyString());
    }


}