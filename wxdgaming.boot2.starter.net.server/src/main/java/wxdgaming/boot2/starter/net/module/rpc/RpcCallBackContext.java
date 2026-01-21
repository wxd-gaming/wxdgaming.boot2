package wxdgaming.boot2.starter.net.module.rpc;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorService;

import java.util.concurrent.CompletableFuture;

/**
 * rpc 回调上下文
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-21 14:46
 */
@Getter
@Setter
public class RpcCallBackContext extends ExecutorEvent {

    private String threadName;
    private final CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();
    private JSONObject responseParams;
    private Throwable responseThrowable;

    public RpcCallBackContext() {
    }

    public void complete(JSONObject jsonObject) {
        responseParams = jsonObject;
        submit();
    }

    public void completeExceptionally(Throwable throwable) {
        responseThrowable = throwable;
        submit();
    }

    @Override public void onEvent() throws Exception {
        if (responseThrowable != null) {
            completableFuture.completeExceptionally(responseThrowable);
            return;
        }
        completableFuture.complete(responseParams);
    }

    @Override public void submit() {
        ExecutorService executorService = ExecutorFactory.getExecutorServiceLogic();
        if (StringUtils.isNotBlank(getThreadName())) {
            executorService = ExecutorFactory.getExecutor(getThreadName());
        }
        executorService.execute(this);
    }

}
