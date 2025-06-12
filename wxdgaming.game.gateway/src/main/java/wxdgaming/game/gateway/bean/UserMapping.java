package wxdgaming.game.gateway.bean;

import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.net.MessageEncode;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.PojoBase;

/**
 * 用户映射关系
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 17:03
 **/
@Getter
@Setter
@Accessors(chain = true)
public class UserMapping extends ObjectBase {

    private String account;
    private long chooseRoleId;
    private int chooseServerId;
    private SocketSession chooseServerSession;
    private SocketSession clientSocketSession;

    public ChannelFuture send2Client(PojoBase pojoBase) {
        return clientSocketSession.write(pojoBase);
    }

    public ChannelFuture send2Client(int messageId, byte[] messages) {
        Object build = MessageEncode.build(getClientSocketSession(), messageId, messages);
        return getClientSocketSession().write(build);
    }

    public ChannelFuture send2Game(PojoBase pojoBase) {
        return getClientSocketSession().write(pojoBase);
    }

    public ChannelFuture send2Game(int messageId, byte[] messages) {
        Object build = MessageEncode.build(getChooseServerSession(), messageId, messages);
        return getChooseServerSession().write(build);
    }

    public long clientSessionId() {
        if (getClientSocketSession() == null) return 0;
        return getClientSocketSession().getUid();
    }

    @Override public String toString() {
        return "UserMapping{account='%s', chooseRoleId=%d, chooseServerId=%d, clientSessionId=%d}"
                .formatted(account, chooseRoleId, chooseServerId, clientSessionId());
    }
}
