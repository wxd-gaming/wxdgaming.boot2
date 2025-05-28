package wxdgaming.game.server.script.buff;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.buff.Buff;
import wxdgaming.game.server.event.OnHeart;

import java.util.ArrayList;

/**
 * buff管理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 10:16
 **/
@Slf4j
@Singleton
public class BuffService {

    @OnHeart
    public void onHeartBuffAction(MapNpc mapNpc, long mill) {
        ArrayList<Buff> buffs = mapNpc.getBuffs();

    }

}
