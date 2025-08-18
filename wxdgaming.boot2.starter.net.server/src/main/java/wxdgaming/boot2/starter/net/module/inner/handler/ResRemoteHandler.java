package wxdgaming.boot2.starter.net.module.inner.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.zip.GzipUtil;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.message.inner.ResRemote;
import wxdgaming.boot2.starter.net.module.rpc.RpcService;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResRemoteHandler {

    final RpcService rpcService;

    @Inject
    public ResRemoteHandler(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    @ProtoRequest(ignoreQueue = true)
    public void resRemote(SocketSession socketSession, ResRemote req) {
        long rpcId = req.getUid();
        String token = req.getToken();

        if (!Objects.equals(rpcService.sign(rpcId), token)) {
            log.error("rpc ({}) 调用异常 token 无效 ", rpcId);
            return;
        }

        String params = req.getParams();
        if (req.getGzip() == 1) {
            params = GzipUtil.unGzip2String(params);
        }
        JSONObject jsonObject = FastJsonUtil.parse(params);
        int code = jsonObject.getIntValue("code");
        CompletableFuture<JSONObject> stringCompletableFuture = rpcService.responseFuture(rpcId);
        if (code == 1) {
            stringCompletableFuture.complete(jsonObject);
        } else {
            RuntimeException ex = new RuntimeException(params);
            ex.setStackTrace(new StackTraceElement[0]);
            stringCompletableFuture.completeExceptionally(ex);
        }
    }

}