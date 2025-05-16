package wxdgaming.boot2.starter.net.module.inner.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.zip.GzipUtil;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.module.inner.*;
import wxdgaming.boot2.starter.net.module.inner.message.ReqRemote;

import java.lang.reflect.InvocationTargetException;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqRemoteHandler {

    final RpcService rpcService;
    final RpcListenerFactory rpcListenerFactory;

    @Inject
    public ReqRemoteHandler(RpcService rpcService, RpcListenerFactory rpcListenerFactory) {
        this.rpcService = rpcService;
        this.rpcListenerFactory = rpcListenerFactory;
    }

    @ProtoRequest(ignoreQueue = true)
    public void reqRemote(SocketSession socketSession, ReqRemote req) {
        long rpcId = req.getUid();
        String cmd = req.getCmd();
        String token = req.getToken();
        int gzip = req.getGzip();
        String params = req.getParams();
        if (gzip == 1) {
            params = GzipUtil.unGzip2String(params);
        }
        if (!cmd.startsWith("/")) cmd = "/" + cmd;
        log.debug("rpcId: {}, cmd: {}, params: {}", rpcId, cmd, params);
        JSONObject paramObject = FastJsonUtil.parse(params);

        try {
            String lowerCase = cmd.toLowerCase();
            RpcListenerContent rpcListenerContent = rpcListenerFactory.getRpcListenerContent();
            RpcMapping rpcMapping = rpcListenerContent.getRpcMappingMap().get(lowerCase);
            if (rpcMapping == null) {
                if (rpcId > 0) {
                    rpcService.response(socketSession, rpcId, RunResult.error(9, "not cmd path"));
                }
                return;
            }

            ThreadContext.cleanup();
            RpcListenerTrigger rpcListenerTrigger = new RpcListenerTrigger(
                    rpcMapping,
                    rpcService,
                    rpcListenerContent.getRunApplication(),
                    socketSession,
                    rpcId,
                    paramObject
            );

            boolean allMatch = rpcListenerContent.getRpcFilterList().stream()
                    .allMatch(filter -> filter.doFilter(rpcListenerTrigger, lowerCase, socketSession, paramObject));
            if (!allMatch) {
                return;
            }

            rpcListenerTrigger.submit();
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = e.getCause();
            }
            log.error("dispatch error rpcId: {}, cmd: {}, paramData: {}", rpcId, cmd, params, e);
            if (rpcId > 0) {
                rpcService.response(socketSession, rpcId, RunResult.error(500, "server error"));
            }
        }

    }

}