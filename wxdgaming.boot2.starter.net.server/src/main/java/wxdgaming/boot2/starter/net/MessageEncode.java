package wxdgaming.boot2.starter.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.pojo.PojoBase;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 消息编码
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-08-20 09:09
 **/
@Slf4j
@Getter
@ChannelHandler.Sharable
public abstract class MessageEncode extends ChannelOutboundHandlerAdapter {

    private final boolean scheduledFlush;
    private final long scheduledDelayMs;

    private final ConcurrentHashMap<EventLoop, HashSet<Channel>> eventLoopFlushChannelMap = new ConcurrentHashMap<>();

    private void addFlushChannel(Channel channel) {
        if (!scheduledFlush) {
            return;
        }
        eventLoopFlushChannelMap.computeIfAbsent(channel.eventLoop(), k -> {
                    HashSet<Channel> objects = new HashSet<>();
                    k.scheduleWithFixedDelay(
                            () -> {
                                objects.forEach(Channel::flush);
                                objects.clear();
                            },
                            scheduledDelayMs, scheduledDelayMs,
                            TimeUnit.MILLISECONDS
                    );
                    return objects;
                })
                .add(channel);
    }

    public MessageEncode(boolean scheduledFlush, long scheduledDelayMs) {
        this.scheduledFlush = scheduledFlush;
        this.scheduledDelayMs = scheduledDelayMs;
    }

    @Override public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.deregister(ctx, promise);
    }

    @Override public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        SocketSession socketSession = ChannelUtil.session(ctx.channel());
        switch (msg) {
            case String str -> {
                if (!socketSession.isWebSocket()) {
                    log.warn("{} ", socketSession, new RuntimeException("不是 websocket 不允许发送 string 类型"));
                    return;
                }
                socketSession.addSendFlowByte(str.length());
                TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(str);
                super.write(ctx, textWebSocketFrame, promise);
            }
            case PojoBase pojoBase -> {
                int msgId = pojoBase.msgId();
                byte[] bytes = pojoBase.encode();
                socketSession.addSendFlowByte(bytes.length);
                Object build = build(socketSession, msgId, bytes);
                super.write(ctx, build, promise);
                if (log.isDebugEnabled()) {
                    log.debug("发送消息：{} msgId={}, {}", socketSession, msgId, pojoBase);
                }
            }
            case byte[] bytes -> {
                socketSession.addSendFlowByte(bytes.length);
                ByteBuf byteBuf = socketSession.getChannel().alloc().buffer(bytes.length);
                byteBuf.writeBytes(bytes);
                if (socketSession.isWebSocket()) {
                    super.write(ctx, new BinaryWebSocketFrame(byteBuf), promise);
                } else {
                    super.write(ctx, byteBuf, promise);
                }
            }
            case ByteBuf byteBuf -> {
                socketSession.addSendFlowByte(byteBuf.readableBytes());
                if (socketSession.isWebSocket()) {
                    super.write(ctx, new BinaryWebSocketFrame(byteBuf), promise);
                } else {
                    super.write(ctx, byteBuf, promise);
                }
            }
            case null, default -> super.write(ctx, msg, promise);
        }
        addFlushChannel(ctx.channel());
    }

    public static Object build(SocketSession session, int messageId, byte[] bytes) {
        ByteBuf byteBuf = session.getChannel().alloc().buffer(bytes.length + 10);
        byteBuf.writeInt(bytes.length + 4);
        byteBuf.writeInt(messageId);
        byteBuf.writeBytes(bytes);
        if (session.isWebSocket()) {
            return new BinaryWebSocketFrame(byteBuf);
        }
        return byteBuf;
    }

}
