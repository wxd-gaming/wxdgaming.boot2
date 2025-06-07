package wxdgaming.game.server.script.http.gm.dynamiccode;

import com.google.inject.Singleton;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Param;
import wxdgaming.boot2.core.chatset.Base64Util;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.loader.ClassBytesLoader;
import wxdgaming.boot2.core.zip.GzipUtil;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.boot2.starter.net.server.http.HttpContext;

import java.util.Map;

@Singleton
@RequestMapping(path = "888")
public class DynamicCodeScript extends HoldRunApplication {

    static final String SIGN = "ABC";

    @HttpRequest()
    public RunResult dynamic(HttpContext httpContext,
                             @Param(path = "sign") String sign,
                             @Param(path = "code") String codeBase64) throws Exception {
        if (!SIGN.equals(sign)) return RunResult.fail("签名错误");
        System.out.println(codeBase64);
        String zipJson = Base64Util.decode(codeBase64);
        String json = GzipUtil.unGzip2String(zipJson);
        Map<String, byte[]> parseMap = FastJsonUtil.parseMap(json, String.class, byte[].class);
        /*map 内容 写入某个文件夹，比如 target/dynamic/ */
        // new URLClassLoader(new URL[]{new URL("file:///target/dynamic/")});
        try (ClassBytesLoader classBytesLoader = new ClassBytesLoader(parseMap, DynamicCodeScript.class.getClassLoader())) {
            classBytesLoader.loadAll();
            Map<String, Class<?>> loadClassMap = classBytesLoader.getLoadClassMap();
            for (Class<?> cls : loadClassMap.values()) {
                if (IGmDynamic.class.isAssignableFrom(cls)) {
                    IGmDynamic gmDynamic = (IGmDynamic) cls.getDeclaredConstructor().newInstance();
                    Object result = gmDynamic.execute(runApplication);
                    System.out.println(result);
                    return RunResult.ok().data(result);
                }
            }
        }
        return RunResult.fail("没有找到对应的动态脚本");
    }

}
