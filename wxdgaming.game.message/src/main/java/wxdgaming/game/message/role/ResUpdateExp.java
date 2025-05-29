package  wxdgaming.game.message.role;

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
import wxdgaming.game.message.global.*;


/** 更新经验 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("更新经验")
public class ResUpdateExp extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 47342601;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 当前经验 */
    @Tag(1) private long exp;


}
