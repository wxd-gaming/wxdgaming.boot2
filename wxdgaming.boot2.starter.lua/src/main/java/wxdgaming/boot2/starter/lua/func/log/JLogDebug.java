package wxdgaming.boot2.starter.lua.func.log;

import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Lua;
import wxdgaming.boot2.starter.lua.LuaInvokeJavaFunction;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * java log info
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-12-26 16:00
 **/
@Slf4j
public class JLogDebug extends LuaInvokeJavaFunction {

    @Override public String cmd() {
        return "ldebug";
    }

    @Override public Object doAction(Lua L, Object[] args) {
        if (log.isDebugEnabled())
            log.debug("{}", Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(" ")));
        return null;
    }

}
