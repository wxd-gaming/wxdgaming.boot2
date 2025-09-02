package remote.code;

import com.alibaba.fastjson.JSONObject;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.game.server.api.TestApi;
import wxdgaming.game.server.script.http.gm.dynamiccode.IGmDynamic;

import java.lang.reflect.Field;

public class ExecGM implements IGmDynamic {


    @Override public Object execute(ApplicationContextProvider runApplication, JSONObject jsonObject) throws Exception {
        TestApi instance = runApplication.getBean(TestApi.class);
        instance.strMap.put("a", "2");
        // player = instance.getPlayer();
        // player.setName("aabb");
        Field str2Map = TestApi.class.getDeclaredField("str2Map");
        str2Map.setAccessible(true);
        Object object = str2Map.get(instance);
        jsonObject.put("str2Map", object);
        return FastJsonUtil.toJSONString(jsonObject);
    }

}
