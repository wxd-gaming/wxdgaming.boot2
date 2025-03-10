package wxdgaming.boot2.starter.net.module.inner;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.cache.Cache;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.format.HexId;
import wxdgaming.boot2.core.zip.GzipUtil;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.module.inner.message.ReqRemote;
import wxdgaming.boot2.starter.net.module.inner.message.ResRemote;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * rpc 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-17 19:25
 **/
@Slf4j
@Singleton
public class RpcService {

    final HexId hexId;
    final RpcListenerFactory rpcListenerFactory;

    final Cache<Long, CompletableFuture<JSONObject>> rpcCache;

    @Inject
    public RpcService(RpcListenerFactory rpcListenerFactory) {
        this.rpcListenerFactory = rpcListenerFactory;
        this.hexId = new HexId(BootConfig.getIns().sid());
        this.rpcCache = Cache.<Long, CompletableFuture<JSONObject>>builder()
                .cacheName("rpc-server")
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .removalListener((key, value) -> {
                    log.debug("rpcCache remove key:{}", key);
                    value.completeExceptionally(new RuntimeException("time out"));
                    return true;
                })
                .build();
    }

    public CompletableFuture<JSONObject> responseFuture(long rpcId) {
        return rpcCache.getIfPresent(rpcId);
    }

    public CompletableFuture<JSONObject> request(SocketSession socketSession, String cmd, Object params) {
        return request(socketSession, cmd, params, !socketSession.isEnabledScheduledFlush());
    }

    public CompletableFuture<JSONObject> request(SocketSession socketSession, String cmd, Object params, boolean immediate) {
        return request(socketSession, cmd, JSONObject.toJSONString(params), immediate);
    }

    public CompletableFuture<JSONObject> request(SocketSession socketSession, String cmd, String params) {
        return request(socketSession, cmd, params, !socketSession.isEnabledScheduledFlush());
    }

    public CompletableFuture<JSONObject> request(SocketSession socketSession, String cmd, String params, boolean immediate) {
        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();
        ReqRemote reqRemote = new ReqRemote();
        reqRemote
                .setCmd(cmd)
                .setParams(params);

        if (reqRemote.getParams().length() > 1024) {
            reqRemote.setGzip(1);
            reqRemote.setParams(GzipUtil.gzip2String(reqRemote.getParams()));
        }
        rpcCache.put(reqRemote.getUid(), completableFuture);
        if (immediate)
            socketSession.writeAndFlush(reqRemote);
        else
            socketSession.write(reqRemote);
        return completableFuture;
    }

    public void response(SocketSession socketSession, long rpcId, Object data) {
        response(socketSession, rpcId, FastJsonUtil.toJson(data), !socketSession.isEnabledScheduledFlush());
    }

    public void response(SocketSession socketSession, long rpcId, Object data, boolean immediate) {
        response(socketSession, rpcId, FastJsonUtil.toJson(data), immediate);
    }

    public void response(SocketSession socketSession, long rpcId, String data, boolean immediate) {
        ResRemote resRemote = new ResRemote();
        resRemote
                .setUid(rpcId)
                .setToken("")
                .setParams(String.valueOf(data));

        if (resRemote.getParams().length() > 1024) {
            resRemote.setGzip(1);
            resRemote.setParams(GzipUtil.gzip2String(resRemote.getParams()));
        }
        if (immediate)
            socketSession.writeAndFlush(resRemote);
        else
            socketSession.write(resRemote);
    }

}
