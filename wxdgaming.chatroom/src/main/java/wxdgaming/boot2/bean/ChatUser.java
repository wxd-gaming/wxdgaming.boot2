package wxdgaming.boot2.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 聊天用户
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-08 10:56
 **/
@Getter
@Setter
public class ChatUser extends ObjectBase {

    private String name;
    private String token;

}
