package wxdgaming.game.server.bean.global;


import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.game.server.bean.global.impl.ServerData;
import wxdgaming.game.server.bean.global.impl.ServerMailData;
import wxdgaming.game.server.bean.global.impl.YunyingData;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 类型
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 10:48
 **/
@Getter
public enum GlobalDataType {
    None(0, "默认值", null, null),
    SERVERDATA(1, "全服数据", ServerData.class, ServerData::new),
    SERVER_MAIL_DATA(2, "全服邮件数据", ServerMailData.class, ServerMailData::new),
    YUNYINGDATA(11, "运营数据", YunyingData.class, YunyingData::new),
    ;

    private static final Map<Integer, GlobalDataType> static_map = MapOf.ofMap(GlobalDataType::getCode, GlobalDataType.values());

    public static GlobalDataType of(int value) {
        return static_map.get(value);
    }

    public static GlobalDataType ofOrException(int value) {
        GlobalDataType tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;
    private final Class<? extends DataBase> dataClass;
    private final Supplier<DataBase> factory;

    GlobalDataType(int code, String comment, Class<? extends DataBase> dataClass, Supplier<DataBase> factory) {
        this.code = code;
        this.comment = comment;
        this.dataClass = dataClass;
        this.factory = factory;
    }

}