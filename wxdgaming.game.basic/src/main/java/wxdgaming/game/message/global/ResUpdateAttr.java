package  wxdgaming.game.message.global;

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


/** 更新属性 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("更新属性")
public class ResUpdateAttr extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 50695681;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 变更的场景对象id */
    @Tag(1) private long uid;
    /** 属性列表 */
    @Tag(2) private List<AttrBean> attrs = new ArrayList<>();


}
