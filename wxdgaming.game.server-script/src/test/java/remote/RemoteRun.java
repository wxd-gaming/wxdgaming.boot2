package remote;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import remote.code.ExecGM;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.util.Base64Util;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.loader.JavaCoderCompile;
import wxdgaming.boot2.core.loader.RemoteClassLoader;
import wxdgaming.boot2.core.zip.GzipUtil;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientScan;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;
import wxdgaming.game.server.script.http.gm.dynamiccode.IGmDynamic;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

/**
 * 远程执行
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-03-29 19:00
 **/
@SpringBootTest(classes = {CoreScan.class, HttpClientScan.class})
public class RemoteRun {

    public void execute(Class<? extends IGmDynamic> cls, JSONObject data) {
        JavaCoderCompile javaCoderCompile = new JavaCoderCompile();
        javaCoderCompile.parentClassLoader(RemoteClassLoader.class.getClassLoader());
        Path path = Path.of("src/test/java", cls.getName().replace(".", "/") + ".java");
        javaCoderCompile.compilerJava(path.toString());
        Map<String, byte[]> bytesMap = javaCoderCompile.toBytesMap();
        String jsonString = FastJsonUtil.toJSONString(bytesMap);
        String zipJson = GzipUtil.gzip2String(jsonString);
        String base64 = Base64Util.encode(zipJson);
        System.out.println(base64);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sign", "ABC");/*签名*/
        jsonObject.put("code", URLEncoder.encode(base64, StandardCharsets.UTF_8));/*代码*/
        jsonObject.put("data", data.toJSONString());/*代码*/

        HttpResponse execute = HttpRequestPost.of("http://localhost:18801/888/dynamic", jsonObject).execute();
        String string = execute.bodyString();
        System.out.println(string);
    }

    @Test
    public void remoteRun() throws Exception {
        execute(ExecGM.class, new JSONObject().fluentPut("cc", "1"));
    }

}
