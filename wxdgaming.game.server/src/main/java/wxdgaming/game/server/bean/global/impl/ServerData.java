package wxdgaming.game.server.bean.global.impl;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.common.bean.global.AbstractGlobalData;
import wxdgaming.game.server.bean.slog.OnlineRecord;

/**
 * 全服数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 10:59
 **/
@Getter
@Setter
public class ServerData extends AbstractGlobalData {

    private OnlineRecord onlineRecord = new OnlineRecord();

}
