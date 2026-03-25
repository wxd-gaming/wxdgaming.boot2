package wxdgaming.game.server.bean.slog;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;

/**
 * 每一天的每一个小数的在线数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-29 20:05
 **/
@Getter
@Setter
public class OnlineRecord extends ObjectBase {

    long onlineSlogTime;
    int onlineSize;
    /**
     * key: 小时
     * value: 在线数据
     */
    HashMap<String, Integer> hourOnlineMap = new HashMap<>();

}
