package wxdgaming.game.test.bean.global;


import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.game.test.bean.global.impl.ServerData;
import wxdgaming.game.test.bean.global.impl.YunyingData;

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
    None(0, "默认值", null),
    ServerData(1, "全服数据", () -> new ServerData()),
    YunyingData(11, "运营数据", () -> new YunyingData()),
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
    private final Supplier<DataBase> supplier;

    GlobalDataType(int code, String comment, Supplier<DataBase> supplier) {
        this.code = code;
        this.comment = comment;
        this.supplier = supplier;
    }

}