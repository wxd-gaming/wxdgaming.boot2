package  wxdgaming.game.message.inner;

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


/** 请求转发消息 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("请求转发消息")
public class InnerForwardMessage extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 56706558;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 转发的sessionId列表 我需要把消息转发给哪些sessionId */
    @Tag(1) private List<Long> sessionIds = new ArrayList<>();
    /** 转发的gameId列表 我需要把消息转发给哪些gameId */
    @Tag(2) private List<Integer> gameIds = new ArrayList<>();
    /** 转发的serverId列表 我需要把消息转发给哪些serverId */
    @Tag(3) private List<Integer> serverIds = new ArrayList<>();
    /** 消息id */
    @Tag(4) private int messageId;
    /** 消息内容 */
    @Tag(5) private byte[] messages;
    /** 转发的rid列表 我需要把消息转发给哪些rid */
    @Tag(6) private List<Long> rids = new ArrayList<>();
    /** kv关系绑定 */
    @Tag(8) private Map<String, String> kvBeansMap = new LinkedHashMap<>();


}
