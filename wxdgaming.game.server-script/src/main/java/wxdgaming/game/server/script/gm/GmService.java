package wxdgaming.game.server.script.gm;

import com.alibaba.fastjson.JSONArray;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.reflect.GuiceReflectContext;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.gm.ann.GM;
import wxdgaming.game.server.script.tips.TipsService;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

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
    private final TipsService tipsService;

    @Inject
    public GmService(TipsService tipsService) {
        this.tipsService = tipsService;
    }

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
        JSONArray jsonArray = new JSONArray(List.of(args));
        String cmd = jsonArray.getString(0).toLowerCase();
        GuiceReflectContext.MethodContent methodContent = gmMap.get(cmd);
        if (methodContent == null) {
            tipsService.tips(player, "不存在的gm命令: " + cmd);
            return;
        }
        Method method = methodContent.getMethod();
        try {
            method.invoke(methodContent.getIns(), player, jsonArray);
        } catch (Exception e) {
            log.error("执行gm命令失败: {}", cmd, e);
            tipsService.tips(player, "执行gm命令失败: " + cmd);
        }
    }

}
