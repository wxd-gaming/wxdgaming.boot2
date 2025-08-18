package  wxdgaming.game.message.task;

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


/** 任务列表 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("任务列表")
public class ReqTaskList extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 46251239;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private TaskType taskType;


}
