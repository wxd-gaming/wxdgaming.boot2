package wxdgaming.boot2.starter.groovy;

import groovy.lang.GroovyShell;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;

/**
 * 服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-03 16:47
 **/
@Service
public class GroovyService extends HoldApplicationContext {
    private int version = 1;

    public Object evaluate(String script) {
        GroovyShell shell = new GroovyShell();
        return shell.evaluate(script);
    }

}
