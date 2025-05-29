package  wxdgaming.game.message.task;

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


/** 接受任务 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("接受任务")
public class ResAcceptTask extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 48289835;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private TaskType taskType;
    /**  */
    @Tag(2) private int taskId;


}
