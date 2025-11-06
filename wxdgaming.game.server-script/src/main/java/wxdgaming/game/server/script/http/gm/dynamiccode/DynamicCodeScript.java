package wxdgaming.game.server.script.http.gm.dynamiccode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.loader.ClassBytesLoader;
import wxdgaming.boot2.core.util.Base64Util;
import wxdgaming.boot2.core.zip.GzipUtil;
import wxdgaming.boot2.starter.groovy.GroovyService;

import java.util.Map;

/**
 * 动态代码
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-20 20:47
 */
@Slf4j
@RestController
@RequestMapping(value = "888")
public class DynamicCodeScript extends HoldApplicationContext {

    static final String SIGN = "ABC";

    final GroovyService groovyService;

    public DynamicCodeScript(GroovyService groovyService) {
        this.groovyService = groovyService;
    }


    @RequestMapping(value = "/dynamic")
    public RunResult dynamic(HttpServletRequest httpContext,
                             @RequestParam(value = "sign") String sign,
                             @RequestParam(value = "data") String dataString,
                             @RequestParam(value = "code") String codeBase64) throws Exception {
        if (!SIGN.equals(sign)) return RunResult.fail("签名错误");
        System.out.println(codeBase64);
        String zipJson = Base64Util.decode(codeBase64);
        String json = GzipUtil.unGzip2String(zipJson);
        Map<String, byte[]> parseMap = FastJsonUtil.parseMap(json, String.class, byte[].class);
        JSONObject data = JSON.parseObject(dataString);
        /*map 内容 写入某个文件夹，比如 target/dynamic/ */
        // new URLClassLoader(new URL[]{new URL("file:///target/dynamic/")});
        try (ClassBytesLoader classBytesLoader = new ClassBytesLoader(parseMap, DynamicCodeScript.class.getClassLoader())) {
            classBytesLoader.loadAll();
            Map<String, Class<?>> loadClassMap = classBytesLoader.getLoadClassMap();
            for (Class<?> cls : loadClassMap.values()) {
                if (IGmDynamic.class.isAssignableFrom(cls)) {
                    IGmDynamic gmDynamic = (IGmDynamic) cls.getDeclaredConstructor().newInstance();
                    Object result = gmDynamic.execute(applicationContextProvider, data);
                    System.out.println(result);
                    return RunResult.ok().data(result);
                }
            }
        }
        return RunResult.fail("没有找到对应的动态脚本");
    }

    @RequestMapping(value = "/groovy")
    public RunResult groovy(HttpServletRequest httpContext,
                            @RequestParam(value = "sign") String sign,
                            @RequestParam(value = "code") String codeBase64) {
        try {
            if (!SIGN.equals(sign)) return RunResult.fail("签名错误");
            String groovyScript = Base64Util.decode(codeBase64);
            Object evaluate = groovyService.evaluate(groovyScript);
            return RunResult.ok().msg("成功").data(evaluate);
        } catch (Exception e) {
            log.error("groovy script", e);
        }
        return RunResult.fail("权限不足");
    }

    @RequestMapping(value = "/v1")
    public String v1() {
        DynamicCodeScript bean = getApplicationContextProvider().getBean(DynamicCodeScript.class);

        return "v6 " + this.equals(bean) + " - " + this.hashCode();
    }

}
