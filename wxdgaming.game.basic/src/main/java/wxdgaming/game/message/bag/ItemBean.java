package  wxdgaming.game.message.bag;

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
public class ItemBean extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 41077900;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private long uid;
    /**  */
    @Tag(2) private int itemId;
    /**  */
    @Tag(3) private long count;
    /**  */
    @Tag(4) private boolean bind;
    /**  */
    @Tag(5) private long expireTime;


}
