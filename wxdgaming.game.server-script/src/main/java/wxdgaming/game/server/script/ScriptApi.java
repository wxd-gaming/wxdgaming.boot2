package wxdgaming.game.server.script;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Body;
import wxdgaming.boot2.core.ann.Param;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.executor.ExecutorConfig;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.boot2.starter.net.ann.RpcRequest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 19:22
 **/
@Slf4j
@Singleton
@RequestMapping(path = "script")
public class ScriptApi {

    @HttpRequest()
    public String index(RunApplication runApplication,
                        @Value(path = "executor") ExecutorConfig executorConfig,
                        @Value(path = "executor1", required = false) ExecutorConfig executorConfig1,
                        @Body(defaultValue = "1") String body,
                        @Param(path = "b1", defaultValue = "2") int b1) {
        return "index";
    }

    @HttpRequest()
    public RunResult stop() {
        Thread.ofPlatform().start(() -> {
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
            System.exit(1);
        });
        return RunResult.ok();
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
    public JSONObject rpcIndex(@Param(path = "a", defaultValue = "2") String a, JSONObject paramData) {
        log.info("{} {} {}", a, paramData, ThreadContext.context().queueName());
        return paramData;
    }

    @RpcRequest
    public JSONObject rpcIndex2(JSONObject paramData) {
        log.info("{} {}", paramData, ThreadContext.context().queueName());
        return paramData;
    }

    // @Scheduled("*/30")
    // public void timer() {
    //     log.info("{}", "timer()");
    // }
    //
    // @Scheduled("*/30")
    // @ExecutorWith(useVirtualThread = true)
    // public void timerAsync() {
    //     log.info("{}", "timerAsync()");
    // }

}
