package wxdgaming.boot2.starter.net.module.inner;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.cache2.CASCache;
import wxdgaming.boot2.core.cache2.Cache;
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
        this.rpcCache = CASCache.<Long, CompletableFuture<JSONObject>>builder()
                .cacheName("rpc-server")
                .heartTimeMs(TimeUnit.SECONDS.toMillis(1))
                .expireAfterWriteMs(TimeUnit.SECONDS.toMillis(60))
                .removalListener((key, value) -> {
                    log.debug("rpcCache remove key:{}", key);
                    value.completeExceptionally(new RuntimeException("time out"));
                    return true;
                })
                .build();
        this.rpcCache.start();
    }

    public CompletableFuture<JSONObject> responseFuture(long rpcId) {
        return rpcCache.getIfPresent(rpcId);
    }

    public Mono<JSONObject> request(SocketSession socketSession, String cmd, Object params) {
        return request(socketSession, cmd, params, !socketSession.isEnabledScheduledFlush());
    }

    public Mono<JSONObject> request(SocketSession socketSession, String cmd, Object params, boolean immediate) {
        return request(socketSession, cmd, JSONObject.toJSONString(params), immediate);
    }

    public Mono<JSONObject> request(SocketSession socketSession, String cmd, String params) {
        return request(socketSession, cmd, params, !socketSession.isEnabledScheduledFlush());
    }

    public Mono<JSONObject> request(SocketSession socketSession, String cmd, String params, boolean immediate) {
        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();
        ReqRemote reqRemote = new ReqRemote();
        reqRemote
                .setUid(hexId.newId())
                .setCmd(cmd)
                .setParams(params);

        if (reqRemote.getParams().length() > 1024) {
            reqRemote.setGzip(1);
            reqRemote.setParams(GzipUtil.gzip2String(reqRemote.getParams()));
        }
        Mono<JSONObject> jsonObjectMono = Mono.fromCompletionStage(completableFuture);
        rpcCache.put(reqRemote.getUid(), completableFuture);
        if (immediate)
            socketSession.writeAndFlush(reqRemote);
        else
            socketSession.write(reqRemote);
        return jsonObjectMono;
    }

    public void response(SocketSession socketSession, long rpcId, Object data) {
        response(socketSession, rpcId, FastJsonUtil.toJSONString(data), !socketSession.isEnabledScheduledFlush());
    }

    public void response(SocketSession socketSession, long rpcId, Object data, boolean immediate) {
        response(socketSession, rpcId, FastJsonUtil.toJSONString(data), immediate);
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
