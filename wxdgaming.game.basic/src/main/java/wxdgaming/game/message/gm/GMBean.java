package  wxdgaming.game.message.gm;

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


/** gm命令 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("gm命令")
public class GMBean extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 37425103;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 客户端用于请求的gm命令名字 */
    @Tag(1) private String cmd;
    /** 客户端展示的gm命令名字 */
    @Tag(2) private String name;
    /** gm命令参数案例 */
    @Tag(3) private String params;


}
