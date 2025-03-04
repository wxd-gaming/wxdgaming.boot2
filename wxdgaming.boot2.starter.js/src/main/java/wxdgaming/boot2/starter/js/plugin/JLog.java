package wxdgaming.boot2.starter.js.plugin;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.js.IJSPlugin;

import java.util.Arrays;
import java.util.stream.Collectors;

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

    public void print(Object... args) {
        System.out.println(Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(" ")));
    }

    public void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    public void debug(String format, Object... args) {
        log.debug(format, args);
    }

    public void info(String format, Object... args) {
        log.info(format, args);
    }

    public void warn(String format, Object... args) {
        log.warn(format, args);
    }


}
