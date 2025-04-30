package wxdgaming.game.test.script.gm;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.reflect.GuiceReflectContext;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.gm.ann.GM;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * gm服务, 运营接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 09:23
 **/
@Slf4j
@Singleton
public class GmService extends HoldRunApplication {

    HashMap<String, GuiceReflectContext.MethodContent> gmMap = new HashMap<>();

    @Init
    public void init() {
        HashMap<String, GuiceReflectContext.MethodContent> tmp = new HashMap<>();
        runApplication.withMethodAnnotated(GM.class)
                .forEach(content -> {
                    Method method = content.getMethod();
                    GuiceReflectContext.MethodContent old = tmp.put(method.getName().toLowerCase(), content);
                    AssertUtil.assertTrue(old == null, "重复的gm命令: " + method.getName());
                });
        gmMap = tmp;
    }

    public void doGm(Player player, String[] args) {

    }

}
