package  wxdgaming.game.message.bag;

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


/** 请求使用道具 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("请求使用道具")
public class ReqUseBagItem extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 46481784;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private BagType bagType;
    /** 道具id，使用数量 */
    @Tag(2) private Map<Long, Long> useMap = new LinkedHashMap<>();


}
