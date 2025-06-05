package  wxdgaming.game.message.bag;

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


/** 导出包名 */
@Getter
@Comment("导出包名")
public enum BagType {

    /**  */
    @Tag(0)
    Bag(0, ""),
    /**  */
    @Tag(1)
    Store(1, ""),

    ;

    private static final Map<Integer, BagType> static_map = MapOf.ofMap(BagType::getCode, BagType.values());

    public static BagType valueOf(int code) {
        return static_map.get(code);
    }

    /** code */
    private final int code;
    /** 备注 */
    private final String command;

    BagType(int code, String command) {
        this.code = code;
        this.command = command;
    }
}
