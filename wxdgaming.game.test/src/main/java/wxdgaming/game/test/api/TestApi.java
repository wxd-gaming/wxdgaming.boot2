package wxdgaming.game.test.api;

import com.google.inject.Singleton;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Body;
import wxdgaming.boot2.core.ann.Param;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.threading.ExecutorConfig;
import wxdgaming.boot2.starter.net.server.ann.HttpRequest;
import wxdgaming.boot2.starter.net.server.ann.RequestMapping;
import wxdgaming.boot2.starter.net.server.ann.RpcRequest;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 19:22
 **/
@Singleton
@RequestMapping(path = "/")
public class TestApi {

    @HttpRequest()
    public String index(RunApplication runApplication,
                        @Value(name = "executor") ExecutorConfig executorConfig,
                        @Value(name = "executor1", required = false) ExecutorConfig executorConfig1,
                        @Body(defaultValue = "1") String body,
                        @Param(name = "b1", defaultValue = "2") String b1) {
        return "index";
    }

    @HttpRequest()
    public RunResult json(RunApplication runApplication,
                          @Value(name = "executor") ExecutorConfig executorConfig,
                          @Value(name = "executor1", required = false) ExecutorConfig executorConfig1,
                          @Body(defaultValue = "1") String body,
                          @Param(name = "b1", defaultValue = "2") String b1) {
        return RunResult.ok();
    }

    @HttpRequest()
    public String error(RunApplication runApplication,
                        @Value(name = "executor") ExecutorConfig executorConfig,
                        @Value(name = "executor1", required = false) ExecutorConfig executorConfig1,
                        @Body(defaultValue = "1") String body,
                        @Param(name = "b1", defaultValue = "2") String b1) {
        throw new RuntimeException("d");
    }

    @RpcRequest
    public String rpcIndex() {

        return "index";
    }

}
