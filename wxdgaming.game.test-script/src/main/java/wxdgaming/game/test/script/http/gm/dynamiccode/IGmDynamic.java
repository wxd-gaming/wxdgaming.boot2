package wxdgaming.game.test.script.http.gm.dynamiccode;

import wxdgaming.boot2.core.RunApplication;

/**
 * gm动态代码
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-29 18:54
 **/
public interface IGmDynamic {

    Object execute(RunApplication runApplication) throws Exception;

}
