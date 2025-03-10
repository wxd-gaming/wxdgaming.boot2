package wxdgaming.boot2.starter.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.pojo.SerializerUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息编码
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-20 09:09
 **/
@Slf4j
@Getter
public abstract class MessageEncode extends ChannelOutboundHandlerAdapter {

    protected final ProtoListenerFactory protoListenerFactory;
    protected final AtomicInteger writeUpdate = new AtomicInteger(0);
    protected long lastUpdateTime = 0;

    public MessageEncode(boolean scheduledFlush, long scheduledDelayMs, Channel channel, ProtoListenerFactory protoListenerFactory) {
        this.protoListenerFactory = protoListenerFactory;
        if (scheduledFlush) {
            /*采用定时器flush，调用 write 而非 writAndFlush 减少网络io开销*/
            ScheduledFuture<?> scheduledFuture = channel.eventLoop().scheduleAtFixedRate(() -> {
                if (lastUpdateTime != writeUpdate.get()) {
                    lastUpdateTime = writeUpdate.get();
                    channel.flush();
                }
            }, scheduledDelayMs, scheduledDelayMs, TimeUnit.MILLISECONDS);
            /*注册事件。 如果连接断开，关闭定时器*/
            channel.closeFuture().addListener(future -> scheduledFuture.cancel(false));
        }
    }

    @Override public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.deregister(ctx, promise);
    }

    @Override public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        SocketSession session = ChannelUtil.session(ctx.channel());
        switch (msg) {
            case String str -> {
                if (!session.isWebSocket()) {
                    log.warn("{} ", session, new RuntimeException("不是 websocket 不允许发送 string 类型"));
                    return;
                }
                TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(str);
                super.write(ctx, textWebSocketFrame, promise);
            }
            case PojoBase pojoBase -> {
                int msgId = protoListenerFactory.messageId(pojoBase.getClass());
                if (msgId == 0) {
                    log.warn("{} 消息 {} 未注册", session, pojoBase.getClass().getName(), new RuntimeException("无法编码"));
                    return;
                }
                byte[] bytes = SerializerUtil.encode(pojoBase);
                ByteBuf byteBuf = build(msgId, bytes);
                if (session.isWebSocket()) {
                    super.write(ctx, new BinaryWebSocketFrame(byteBuf), promise);
                } else {
                    super.write(ctx, byteBuf, promise);
                }
                if (log.isDebugEnabled()) {
                    log.debug("发送消息：{} {}, {}", session, msgId, pojoBase.getClass().getSimpleName());
                }
            }
            case byte[] bytes -> {
                ByteBuf byteBuf = ByteBufUtil.pooledByteBuf(bytes.length);
                byteBuf.writeBytes(bytes);
                super.write(ctx, byteBuf, promise);
            }
            case ByteBuf byteBuf -> {
                if (session.isWebSocket()) {
                    super.write(ctx, new BinaryWebSocketFrame(byteBuf), promise);
                } else {
                    super.write(ctx, byteBuf, promise);
                }
            }
            case null, default -> super.write(ctx, msg, promise);
        }
        writeUpdate.incrementAndGet();
    }

    public static ByteBuf build(int messageId, byte[] bytes) {
        ByteBuf byteBuf = ByteBufUtil.pooledByteBuf(bytes.length + 10);
        byteBuf.writeInt(bytes.length + 4);
        byteBuf.writeInt(messageId);
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }

}
