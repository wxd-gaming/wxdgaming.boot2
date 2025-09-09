package wxdgaming.game.login.bean.global;

import lombok.Getter;
import wxdgaming.game.common.bean.global.AbstractGlobalData;
import wxdgaming.game.common.bean.global.IGlobalDataConst;

import java.util.function.Supplier;

/**
 * 全局数据常量
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-08 14:54
 */
@Getter
public enum GlobalDataConst implements IGlobalDataConst {

    ServerNameGlobalData(1, "服务器冠名数据记录", ServerShowNameGlobalData.class, ServerShowNameGlobalData::new),
    ;

    private final int code;
    private final String comment;
    private final Class<? extends AbstractGlobalData> cls;
    private final Supplier<AbstractGlobalData> factory;

    GlobalDataConst(int code, String comment, Class<? extends AbstractGlobalData> cls, Supplier<AbstractGlobalData> factory) {
        this.code = code;
        this.comment = comment;
        this.cls = cls;
        this.factory = factory;
    }

}
