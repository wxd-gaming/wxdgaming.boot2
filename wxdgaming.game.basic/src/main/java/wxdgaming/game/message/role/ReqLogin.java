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


/** 登录请求 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("登录请求")
public class ReqLogin extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 42828529;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private String account;
    /** 用于验证的令牌 */
    @Tag(2) private String token;
    /** 当前选择的区服id */
    @Tag(3) private int sid;
    /** 客户端特殊列表 */
    @Tag(4) private List<MapBean> clientParams = new ArrayList<>();


}
