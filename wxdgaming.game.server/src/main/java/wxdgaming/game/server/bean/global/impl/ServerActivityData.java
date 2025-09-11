package wxdgaming.game.server.bean.global.impl;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.common.bean.global.AbstractGlobalData;
import wxdgaming.game.server.bean.activity.ActivityData;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器活动数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 10:59
 **/
@Getter
@Setter
public class ServerActivityData extends AbstractGlobalData {

    /**
     * 活动记录
     * <p>key: 活动类型
     * <p>value: 活动数据
     */
    private ConcurrentHashMap<Integer, ActivityData> activityDataMap = new ConcurrentHashMap<>();

}
