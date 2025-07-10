package wxdgaming.boot2.starter.net;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentLoopList;

import java.util.concurrent.ConcurrentHashMap;

/**
 * channel 列表
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-09 16:04
 **/
@Getter
public class SessionGroup extends ConcurrentLoopList<SocketSession> {

    protected final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    protected final ConcurrentHashMap<Long, SocketSession> channelMap = new ConcurrentHashMap<>();

    @Override public boolean add(SocketSession session) {
        channelGroup.add(session.getChannel());
        channelMap.put(session.getUid(), session);
        boolean add = super.add(session);
        session.getChannel().closeFuture().addListener(future -> remove(session));
        return add;
    }

    @Override public boolean remove(SocketSession session) {
        channelGroup.remove(session.getChannel());
        channelMap.remove(session.getUid());
        return super.remove(session);
    }

    /** 空闲 如果 null 触发异常 */
    public SocketSession idleNullException() {
        return loopNullException();
    }

    /** 空闲 */
    public SocketSession idle() {
        return loop();
    }

    public void write(Object message) {
        channelGroup.write(message);
    }

    public void writeAndFlush(Object message) {
        channelGroup.writeAndFlush(message);
    }

    public void flush() {
        channelGroup.flush();
    }

    public int size() {
        return channelGroup.size();
    }

}
