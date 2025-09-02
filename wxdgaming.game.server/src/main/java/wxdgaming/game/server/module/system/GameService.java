package wxdgaming.game.server.module.system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.Tick;
import wxdgaming.game.server.GameServerApplication;

import java.util.concurrent.TimeUnit;

/**
 * 服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-02 10:57
 **/
@Slf4j
@Service
public class GameService implements InitPrint {

    /** 控制一下，5分钟才能加载一次 */
    private final Tick loadScriptTick = new Tick(5, TimeUnit.MINUTES);

    public GameService() {
    }

    public String reloadScript() {
        if (!loadScriptTick.need()) {
            return "不满足间隔时间，不允许重新加载";
        }
        try {
            GameServerApplication.loadScript();
            return "加载成功";
        } catch (Exception e) {
            log.error("加载失败", e);
            return "加载失败";
        }
    }

}
