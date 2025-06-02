package wxdgaming.game.server.bean.task;


import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;

import java.util.Map;

/**
 * 任务类型
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-02 10:25
 **/
@Getter
public enum TaskType {

    Main(1, "主线任务"),
    Guild(2, "公会任务"),
    ;

    private static final Map<Integer, TaskType> static_map = MapOf.ofMap(TaskType::getCode, TaskType.values());

    public static TaskType of(int value) {
        return static_map.get(value);
    }

    public static TaskType ofOrException(int value) {
        TaskType tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;

    TaskType(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

}