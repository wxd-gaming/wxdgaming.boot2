package wxdgaming.game.test.script.buff;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.test.bean.MapNpc;
import wxdgaming.game.test.event.OnHeart;

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

    }

}
