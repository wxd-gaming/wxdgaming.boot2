package  wxdgaming.game.message.tips;

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


/** 提示内容 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("提示内容")
public class ResTips extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 42002782;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private TipsType type;
    /**  */
    @Tag(2) private String content;
    /**  */
    @Tag(3) private List<String> params = new ArrayList<>();
    /** 提示消息id，如果客户端在监听这个id */
    @Tag(4) private int resMessageId;


}
