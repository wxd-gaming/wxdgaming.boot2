package wxdgaming.game.server.bean.global;


import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.game.bean.global.AbstractGlobalData;
import wxdgaming.game.common.global.IGlobalDataConst;
import wxdgaming.game.server.bean.global.impl.ServerData;
import wxdgaming.game.server.bean.global.impl.ServerMailData;
import wxdgaming.game.server.bean.global.impl.YunyingData;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 类型
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 10:48
 **/
@Getter
public enum GlobalDataConst implements IGlobalDataConst {
    None(0, "默认值", null, null),
    SERVERDATA(1, "全服数据", ServerData.class, ServerData::new),
    SERVER_MAIL_DATA(2, "全服邮件数据", ServerMailData.class, ServerMailData::new),
    YUNYINGDATA(11, "运营数据", YunyingData.class, YunyingData::new),
    ;

    private static final Map<Integer, GlobalDataConst> static_map = MapOf.ofMap(GlobalDataConst::getCode, GlobalDataConst.values());

    public static GlobalDataConst of(int value) {
        return static_map.get(value);
    }

    public static GlobalDataConst ofOrException(int value) {
        GlobalDataConst tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

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