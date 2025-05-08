package wxdgaming.boot2.starter.net;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.lang.TickCount;
import wxdgaming.boot2.starter.net.pojo.PojoBase;

import java.util.concurrent.atomic.AtomicLong;

/**
 * socket session
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-16 08:58
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class SocketSession {

    public enum Type {
        server,
        client,
    }

    private static final AtomicLong ATOMIC_LONG = new AtomicLong();

    private final long uid;
    private final Type type;
    private final Channel channel;
    private boolean webSocket;
    private boolean ssl;
    private final boolean enabledScheduledFlush;
    /** 每秒钟帧的最大数量 */
    private int maxFrameLength = -1;
    /** 帧最大字节数 */
    private long maxFrameBytes = -1;
    private final TickCount receiveMessageTick = new TickCount(1000);
    private final JSONObject bindData = new JSONObject();

    public SocketSession(Type type, Channel channel, Boolean webSocket, boolean enabledScheduledFlush) {
        this.uid = ATOMIC_LONG.get();
        this.type = type;
        this.channel = channel;
        this.webSocket = Boolean.TRUE.equals(webSocket);
        this.enabledScheduledFlush = enabledScheduledFlush;
        ChannelUtil.attr(this.channel, ChannelUtil.SOCKET_SESSION_KEY, this);
    }

    public <R> R bindData(String key) {
        return (R) bindData.get(key);
    }

    public SocketSession bindData(String key, Object value) {
        bindData.put(key, value);
        return this;
    }

    public String getIP() {
        return ChannelUtil.getIP(this.channel);
    }

    public String getLocalAddress() {
        return ChannelUtil.getLocalAddress(this.channel);
    }

    public String getRemoteAddress() {
        return ChannelUtil.getRemoteAddress(this.channel);
    }

    public ChannelFuture writeAndFlush(Object message) {
        return channel.writeAndFlush(message);
    }

    public ChannelFuture writeAndFlush(PojoBase pojoBase) {
        return channel.writeAndFlush(pojoBase);
    }

    public ChannelFuture writeAndFlush(ByteBuf byteBuf) {
        if (webSocket) {
            BinaryWebSocketFrame webSocketFrame = new BinaryWebSocketFrame(byteBuf);
            return channel.writeAndFlush(webSocketFrame);
        } else {
            return channel.writeAndFlush(byteBuf);
        }
    }

    public ChannelFuture write(Object message) {
        return channel.write(message);
    }

    public ChannelFuture write(PojoBase pojoBase) {
        return channel.write(pojoBase);
    }

    public ChannelFuture write(ByteBuf byteBuf) {
        if (webSocket) {
            BinaryWebSocketFrame webSocketFrame = new BinaryWebSocketFrame(byteBuf);
            return channel.write(webSocketFrame);
        } else {
            return channel.write(byteBuf);
        }
    }

    public void flush() {
        channel.flush();
    }

    /** 是否可用 */
    public boolean isOpen() {
        return channel.isRegistered() && channel.isOpen();
    }

    public void close(String string) {
        ChannelUtil.closeSession(channel, toString() + " " + string);
    }

    /** 增加接受消息的次数 */
    public boolean checkReceiveMessage(int c) {
        if (getMaxFrameBytes() > 0 && getMaxFrameBytes() < c) {
            close("超过最大帧字节数");
            return false;
        }
        long added = receiveMessageTick.add(1);
        if (getMaxFrameLength() > 0 && added > getMaxFrameLength()) {
            close("超过最大帧数量");
            return false;
        }
        return true;
    }

    @Override public String toString() {
        return """
                【%s - %s%s%s%s】""".formatted(
                type.name(),
                ChannelUtil.ctxTostring(channel),
                getBindData().isEmpty() ? "" : ", bindData: " + getBindData().entrySet().stream().map(v->v.getKey() + "=" + v.getValue()).reduce((a,b)->a + "," + b).orElse(""),
                isWebSocket() ? ", websocket" : "",
                isSsl() ? ", ssl" : ""
        );
    }
}
