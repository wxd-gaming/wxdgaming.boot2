package gm;

import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.game.test.api.TestApi;
import wxdgaming.game.test.script.gm.IGmDynamic;

import java.lang.reflect.Field;

public class ExecGM implements IGmDynamic {


    @Override public Object execute(RunApplication runApplication) throws Exception {
        TestApi instance = runApplication.getInstance(TestApi.class);
        instance.strMap.put("a", "2");
        // player = instance.getPlayer();
        // player.setName("aabb");
        Field str2Map = TestApi.class.getDeclaredField("str2Map");
        Object object = str2Map.get(instance);

        return FastJsonUtil.toJSONString(instance.strMap);
    }

}
