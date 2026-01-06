package remote.code;

import com.alibaba.fastjson2.JSONObject;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.game.server.script.http.gm.dynamiccode.IGmDynamic;

public class TestGM implements IGmDynamic {


    @Override public Object execute(ApplicationContextProvider runApplication, JSONObject jsonObject) {
        return "远程3";
    }

}
