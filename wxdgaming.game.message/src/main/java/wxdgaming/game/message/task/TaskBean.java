package wxdgaming.game.message.task;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.ann.Comment;
import wxdgaming.boot2.starter.net.pojo.PojoBase;

import java.util.ArrayList;
import java.util.List;


/** null */
@Getter
@Setter
@Accessors(chain = true)
@Comment("null")
public class TaskBean extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 42597836;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(2) private int taskId;
    /**  */
    @Tag(3) private List<Long> progresses = new ArrayList<>();


}
