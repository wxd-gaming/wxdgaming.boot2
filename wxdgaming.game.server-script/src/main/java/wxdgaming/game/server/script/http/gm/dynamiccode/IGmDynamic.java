package wxdgaming.game.server.script.http.gm.dynamiccode;


import com.alibaba.fastjson2.JSONObject;
import wxdgaming.boot2.core.ApplicationContextProvider;

/**
 * gm动态代码
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-03-29 18:54
 **/
public interface IGmDynamic {

    Object execute(ApplicationContextProvider runApplication, JSONObject jsonObject) throws Exception;

}
