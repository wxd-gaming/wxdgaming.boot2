package wxdgaming.boot2.starter.lua.func.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.lua.LuaInvokeJavaFunction;
import wxdgaming.boot2.starter.lua.LuaToJavaDTO;

import java.util.stream.Collectors;

/**
 * java log info
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-12-26 16:00
 **/
@Slf4j
@Component
public class JLogInfo extends LuaInvokeJavaFunction {


    @Override public String cmd() {
        return "linfo";
    }

    @Override public Object doAction(LuaToJavaDTO luaToJavaDTO) {
        log.info("{}", luaToJavaDTO.args().stream().map(String::valueOf).collect(Collectors.joining(" ")));
        return null;
    }

}
