package wxdgaming.game.common.bean.ban;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;

import java.util.Map;

/**
 * 禁止类型
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-29 15:31
 **/
@Getter
public enum BanType {
    None(0, "默认值"),
    AccountLogin(1, "账号登录"),
    AccountChat(2, "账号聊天"),
    RoleLogin(3, "角色登录"),
    RoleChat(4, "角色聊天"),
    ;

    private final int code;
    private final String comment;

    BanType(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

}