package executor;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;

import java.util.Map;

/**
 * 心跳
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 20:49
 **/
@Getter
public enum HeartConst {
    Heart(0, "心跳"),
    Second(1, "每秒"),
    Minute(2, "每分钟"),
    Hour(3, "每小时"),
    DayEnd(4, "每天结束"),
    Week(5, "每周"),
    ;

    private static final Map<Integer, HeartConst> static_map = MapOf.ofMap(HeartConst::getCode, HeartConst.values());

    public static HeartConst of(int value) {
        return static_map.get(value);
    }

    public static HeartConst ofOrException(int value) {
        HeartConst tmp = static_map.get(value);
        if (tmp == null) throw new IllegalArgumentException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;

    HeartConst(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

}