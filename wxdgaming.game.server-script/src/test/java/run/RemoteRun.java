package run;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import wxdgaming.boot2.core.chatset.Base64Util;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.loader.JavaCoderCompile;
import wxdgaming.boot2.core.loader.RemoteClassLoader;
import wxdgaming.boot2.core.zip.GzipUtil;
import wxdgaming.boot2.starter.net.httpclient5.HttpContent;
import wxdgaming.boot2.starter.net.httpclient5.PostRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 远程执行
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-29 19:00
 **/
public class RemoteRun {

    @Test
    public void remoteRun() throws Exception {

        JavaCoderCompile javaCoderCompile = new JavaCoderCompile();
        javaCoderCompile.parentClassLoader(RemoteClassLoader.class.getClassLoader());
        javaCoderCompile.compilerJava("src/test/java/gm/ExecGM.java");
        Map<String, byte[]> bytesMap = javaCoderCompile.toBytesMap();
        String jsonString = FastJsonUtil.toJSONString(bytesMap);
        String zipJson = GzipUtil.gzip2String(jsonString);
        String base64 = Base64Util.encode(zipJson);
        System.out.println(base64);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sign", "ABC");/*签名*/
        jsonObject.put("code", URLEncoder.encode(base64, StandardCharsets.UTF_8));/*代码*/

        HttpContent execute = PostRequest.ofJson("http://localhost:8000/888/dynamic", jsonObject.toJSONString()).execute();
        String string = execute.bodyString();
        System.out.println(string);

    }

}
