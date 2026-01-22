package wxdgaming.game.server.api;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.net.ann.RpcRequest;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-14 19:22
 **/
@Slf4j
@RestController
@RequestMapping(value = "/")
public class TestApi extends HoldApplicationContext {


    public ConcurrentHashMap<String, String> strMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> str2Map = new ConcurrentHashMap<>();

    public TestApi() {
        strMap.put("a", "b");
        str2Map.put("a", "b");
    }

    @RequestMapping(value = "user/login")
    public String userLogin() {
        return "index";
    }

    @RequestMapping("/index")
    public String index(@RequestBody() String body,
                        @RequestParam(value = "b1", defaultValue = "2") int b1) {
        return "index";
    }

    @RequestMapping("/json")
    public RunResult json(@RequestBody() String body,
                          @RequestParam(value = "b1", defaultValue = "2") String b1) {
        return RunResult.ok();
    }

    @RpcRequest
    public JSONObject rpcIndex(JSONObject paramData) {
        log.debug("{} {}", paramData, ThreadContext.context().threadVO().getQueueName());
        return paramData;
    }

    @RpcRequest
    public JSONObject rpcIndex2(JSONObject paramData, @RequestParam(value = "a") String a) {
        log.debug("{} {} {}", a, paramData, ThreadContext.context().threadVO().getQueueName());
        return paramData;
    }

    @Scheduled("0 0")
    public void timer() {
        log.debug("{}", "timer()");
        getApplicationContextProvider().executeMethodWithAnnotated(RunTest.class, 1, 2);
    }

    @Documented
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Target({java.lang.annotation.ElementType.METHOD})
    @interface RunTest {

    }

    @RunTest
    public void runTestParam(int a, int b) {
        log.info("{} a={}, b={}", "runTest()", a, b);
    }

    // @Scheduled("*/30")
    // @ExecutorWith(useVirtualThread = true)
    // public void timerAsync() {
    //     log.info("{}", "timerAsync()");
    // }


    public void print() {
        log.info("{} {}", "print()", FastJsonUtil.toJSONString(strMap));
    }

}
