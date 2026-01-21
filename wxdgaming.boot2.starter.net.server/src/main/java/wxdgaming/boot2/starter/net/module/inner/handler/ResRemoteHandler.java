package wxdgaming.boot2.starter.net.module.inner.handler;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.zip.GzipUtil;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.message.inner.ResRemote;
import wxdgaming.boot2.starter.net.module.rpc.RpcCallBackContext;
import wxdgaming.boot2.starter.net.module.rpc.RpcService;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;

import java.util.Objects;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResRemoteHandler {

    final RpcService rpcService;

    public ResRemoteHandler(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    @ProtoRequest(value = ResRemote.class, ignoreQueue = true)
    public void resRemote(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ResRemote req = event.buildMessage();
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
        RpcCallBackContext rpcCallBackContext = rpcService.responseFuture(rpcId);
        if (code == 1) {
            rpcCallBackContext.complete(jsonObject);
        } else {
            RuntimeException ex = new RuntimeException(params);
            ex.setStackTrace(new StackTraceElement[0]);
            rpcCallBackContext.completeExceptionally(ex);
        }
    }

}