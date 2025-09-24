package wxdgaming.game.server.script.gm;

import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.lang.AssertException;
import wxdgaming.game.message.gm.GMBean;
import wxdgaming.game.message.gm.GmGroup;
import wxdgaming.game.message.gm.ResGmList;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.gm.ann.GM;
import wxdgaming.game.server.script.tips.TipsService;

import java.lang.reflect.Method;
import java.util.*;

/**
 * gm服务, 运营接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 09:23
 **/
@Slf4j
@Getter
@Service
public class GmService extends HoldApplicationContext {

    final GameServerProperties gameServerProperties;
    final TipsService tipsService;
    Map<String, ApplicationContextProvider.ProviderMethod> gmMap = new HashMap<>();
    ResGmList resGmList = new ResGmList();

    public GmService(GameServerProperties gameServerProperties, TipsService tipsService) {
        this.gameServerProperties = gameServerProperties;
        this.tipsService = tipsService;
    }

    @Init
    public void init() {
        gmMap = applicationContextProvider.toMapWithMethodAnnotated(
                GM.class,
                providerMethod -> providerMethod.getMethod().getName().toLowerCase(),
                providerMethod -> providerMethod
        );

        TreeMap<String, List<GMBean>> gmList = new TreeMap<>();

        gmMap.entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .forEach((entry) -> {
                    String cmd = entry.getKey();
                    ApplicationContextProvider.ProviderMethod providerMethod = entry.getValue();
                    Method method = providerMethod.getMethod();
                    GM annotation = method.getAnnotation(GM.class);
                    GMBean gmBean = new GMBean();
                    gmBean.setCmd(cmd);
                    gmBean.setName(StringUtils.isBlank(annotation.name()) ? cmd : annotation.name());
                    gmBean.setParams(annotation.param());
                    gmList.computeIfAbsent(annotation.group(), k -> new ArrayList<>()).add(gmBean);
                });

        ResGmList tmpResGmList = new ResGmList();
        for (Map.Entry<String, List<GMBean>> entry : gmList.entrySet()) {
            tmpResGmList.getGmGroupList().add(new GmGroup().setGroup(entry.getKey()).setGmList(entry.getValue()));
        }
        resGmList = tmpResGmList;
    }

    public void doGm(Player player, String[] args) {
        JSONArray jsonArray = new JSONArray(List.of(args));
        String cmd = jsonArray.getString(0).toLowerCase();
        ApplicationContextProvider.ProviderMethod providerMethod = gmMap.get(cmd);
        if (providerMethod == null) {
            tipsService.tips(player, "不存在的gm命令: " + cmd);
            return;
        }
        GM annotation = providerMethod.getMethod().getAnnotation(GM.class);
        if (!gameServerProperties.isDebug() && annotation.level() > player.getUserMapping().getUserDataVo().getGmLevel()) {
            tipsService.tips(player, "权限不足: " + cmd);
            return;
        }
        try {
            providerMethod.invoke(player, jsonArray);
        } catch (Exception e) {
            log.error("执行gm命令失败: {}", cmd, e);
            if (e instanceof AssertException assertException) {
                cmd = cmd + ", " + assertException.getMessage();
            }
            tipsService.tips(player, "执行gm命令失败: " + cmd);
        }
    }

}
