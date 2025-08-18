package  wxdgaming.game.message.inner;

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


/** 服务类型 */
@Getter
@Comment("服务类型")
public enum ServiceType {

    /** 网关 */
    @Tag(0)
    GATEWAY(0, "网关"),
    /** 登录 */
    @Tag(1)
    LOGIN(1, "登录"),
    /** 游戏 */
    @Tag(2)
    GAME(2, "游戏"),
    /** 匹配 */
    @Tag(3)
    MATCH(3, "匹配"),
    /** 社交 */
    @Tag(4)
    CHAT(4, "社交"),
    /** 邮件 */
    @Tag(5)
    MAIL(5, "邮件"),

    ;

    private static final Map<Integer, ServiceType> static_map = MapOf.ofMap(ServiceType::getCode, ServiceType.values());

    public static ServiceType valueOf(int code) {
        return static_map.get(code);
    }

    /** code */
    private final int code;
    /** 备注 */
    private final String command;

    ServiceType(int code, String command) {
        this.code = code;
        this.command = command;
    }
}
