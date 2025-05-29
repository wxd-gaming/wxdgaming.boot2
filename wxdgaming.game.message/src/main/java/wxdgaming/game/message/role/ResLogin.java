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


/** 登录响应 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("登录响应")
public class ResLogin extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 42848768;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 用户id */
    @Tag(1) private String userId;
    /** 账号 */
    @Tag(2) private String account;
    /**  */
    @Tag(3) private List<RoleBean> roles = new ArrayList<>();


}
