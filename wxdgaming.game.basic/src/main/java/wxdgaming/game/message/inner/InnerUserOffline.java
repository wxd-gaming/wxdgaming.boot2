package  wxdgaming.game.message.inner;

import io.protostuff.Tag;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.ann.Comment;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.starter.net.pojo.PojoBase;


/** 玩家离线 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("玩家离线")
public class InnerUserOffline extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 53197558;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 客户端sessionId */
    @Tag(1) private long clientSessionId;
    /** 账号 */
    @Tag(2) private String account;


}
