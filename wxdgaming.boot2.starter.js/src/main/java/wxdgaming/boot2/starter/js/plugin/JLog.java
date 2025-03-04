package wxdgaming.boot2.starter.js.plugin;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.js.IJSPlugin;

/**
 * java log
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-04 09:18
 **/
@Slf4j
public class JLog implements IJSPlugin {

    @Override public String getName() {
        return "jlog";
    }

    public void debug(String msg) {
        log.debug(msg);
    }

    public void info(String msg) {
        log.info(msg);
    }

    public void warn(String msg) {
        log.warn(msg);
    }


}
