package  wxdgaming.game.message.role;

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
import wxdgaming.game.message.global.*;


/** 创建角色 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("创建角色")
public class ReqCreateRole extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 48291861;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 角色名 */
    @Tag(1) private String name;
    /** 性别 */
    @Tag(2) private int sex;
    /** 职业 */
    @Tag(3) private int job;


}
