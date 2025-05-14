package wxdgaming.game.test.api;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Body;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Param;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.threading.ExecutorConfig;
import wxdgaming.boot2.core.threading.ThreadContext;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.boot2.starter.net.ann.RpcRequest;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 19:22
 **/
@Slf4j
@Singleton
@RequestMapping(path = "/")
public class TestApi {

    RunApplication runApplication;

    public ConcurrentHashMap<String, String> strMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> str2Map = new ConcurrentHashMap<>();

    public TestApi() {
        strMap.put("a", "b");
        str2Map.put("a", "b");
    }

    @Init
    public void init(RunApplication runApplication) {
        this.runApplication = runApplication;
    }

    @HttpRequest()
    public String index(RunApplication runApplication,
                        @Value(path = "executor") ExecutorConfig executorConfig,
                        @Value(path = "executor1", required = false) ExecutorConfig executorConfig1,
                        @Body(defaultValue = "1") String body,
                        @Param(path = "b1", defaultValue = "2") int b1) {
        return "index";
    }

    @HttpRequest()
    public RunResult json(RunApplication runApplication,
                          @Value(path = "executor") ExecutorConfig executorConfig,
                          @Value(path = "executor1", required = false) ExecutorConfig executorConfig1,
                          @Body(defaultValue = "1") String body,
                          @Param(path = "b1", defaultValue = "2") String b1) {
        return RunResult.ok();
    }

    @HttpRequest()
    public String error(RunApplication runApplication,
                        @Value(path = "executor") ExecutorConfig executorConfig,
                        @Value(path = "executor1", required = false) ExecutorConfig executorConfig1,
                        @Body(defaultValue = "1") String body,
                        @Param(path = "b1", defaultValue = "2") String b1) {
        throw new RuntimeException("d");
    }

    @RpcRequest
    public JSONObject rpcIndex(JSONObject paramData) {
        log.debug("{} {}", paramData, ThreadContext.context().queueName());
        return paramData;
    }

    @RpcRequest
    public JSONObject rpcIndex2(JSONObject paramData, @Param(path = "a", nestedPath = true) String a) {
        log.debug("{} {} {}", a, paramData, ThreadContext.context().queueName());
        return paramData;
    }

    @Scheduled("0 0")
    public void timer() {
        log.debug("{}", "timer()");
        runApplication.executeMethodWithAnnotated(RunTest.class, 1, 2);
    }

    @Documented
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Target({java.lang.annotation.ElementType.METHOD})
    @interface RunTest {

    }

    @RunTest
    public void runTestParam(@Value(path = "sid") int sid, int a, int b) {
        log.info("{} sid={}, a={}, b={}", "runTest()", sid, a, b);
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
