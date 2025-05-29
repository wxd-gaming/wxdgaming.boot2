package  wxdgaming.game.message.chat;

import io.protostuff.Tag;
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


/** 请求聊天 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("请求聊天")
public class ReqChatMessage extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 49166150;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private ChatType type;
    /**  */
    @Tag(2) private String content;
    /**  */
    @Tag(3) private List<String> params = new ArrayList<>();
    /** 私聊就是目标玩家id，公会就是公会id */
    @Tag(4) private long targetId;


}
