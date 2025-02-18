package wxdgaming.game.test.api;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Body;
import wxdgaming.boot2.core.ann.Param;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.threading.ExecutorConfig;
import wxdgaming.boot2.core.threading.ExecutorWith;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.boot2.starter.net.ann.RpcRequest;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 19:22
 **/
@Slf4j
@Singleton
@RequestMapping(path = "/")
public class TestApi {

    @HttpRequest()
    public String index(RunApplication runApplication,
                        @Value(name = "executor") ExecutorConfig executorConfig,
                        @Value(name = "executor1", required = false) ExecutorConfig executorConfig1,
                        @Body(defaultValue = "1") String body,
                        @Param(value = "b1", defaultValue = "2") int b1) {
        return "index";
    }

    @HttpRequest()
    public RunResult json(RunApplication runApplication,
                          @Value(name = "executor") ExecutorConfig executorConfig,
                          @Value(name = "executor1", required = false) ExecutorConfig executorConfig1,
                          @Body(defaultValue = "1") String body,
                          @Param(value = "b1", defaultValue = "2") String b1) {
        return RunResult.ok();
    }

    @HttpRequest()
    public String error(RunApplication runApplication,
                        @Value(name = "executor") ExecutorConfig executorConfig,
                        @Value(name = "executor1", required = false) ExecutorConfig executorConfig1,
                        @Body(defaultValue = "1") String body,
                        @Param(value = "b1", defaultValue = "2") String b1) {
        throw new RuntimeException("d");
    }

    @RpcRequest
    public String rpcIndex(JSONObject paramData) {
        log.info("{}", paramData);
        throw new RuntimeException("test");
    }

    @RpcRequest
    public String rpcIndex2(JSONObject paramData) {
        log.info("{}", paramData);
        return "ok";
    }

    @Scheduled("*/10")
    public void timer() {
        log.info("{}", "timer()");
    }

    @Scheduled("*")
    @ExecutorWith(useVirtualThread = true)
    public void timerAsync() {
        log.info("{}", "timerAsync()");
    }

}
