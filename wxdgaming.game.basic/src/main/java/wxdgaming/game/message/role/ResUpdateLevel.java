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


/** 更新等级 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("更新等级")
public class ResUpdateLevel extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 49600219;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 当前等级 */
    @Tag(1) private int level;
    /** 原因 */
    @Tag(2) private String reason;


}
