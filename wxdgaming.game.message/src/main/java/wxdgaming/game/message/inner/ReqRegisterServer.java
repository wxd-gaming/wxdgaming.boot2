package wxdgaming.game.message.inner;

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


/** null */
@Getter
@Setter
@Accessors(chain = true)
@Comment("null")
public class ReqRegisterServer extends PojoBase {

    /** 游戏id */
    @Tag(1) private int gameId;
    /** 主服务器id */
    @Tag(2) private int mainSid;
    /** 服务器id,因为合服可能会导致多个服务器id */
    @Tag(3) private List<Integer> serverIds = new ArrayList<>();
    /** 监听的消息id列表 */
    @Tag(4) private List<Integer> messageIds = new ArrayList<>();
    /** 服务类型 */
    @Tag(5) private ServiceType serviceType;

}
