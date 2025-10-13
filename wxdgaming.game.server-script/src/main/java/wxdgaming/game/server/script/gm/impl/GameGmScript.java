package wxdgaming.game.server.script.gm.impl;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.gm.ann.GM;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 游戏系统相关的gm命令
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-02 14:48
 **/
@Slf4j
@Component
public class GameGmScript extends HoldApplicationContext {

    @GM(group = "系统设置", name = "设置时间", level = 999, param = "时间格式: yyyy-MM-dd HH:mm:ss")
    public void time(Player player, JSONArray args) {
        String timeString = args.getString(1) + " " + args.getString(2);
        Date date = MyClock.parseDate(MyClock.SDF_YYYYMMDDHHMMSS_2, timeString);
        long time = date.getTime();
        AssertUtil.isTrue(time > MyClock.millis(), "时间不允许回调");
        MyClock.TimeOffset.set(time - System.currentTimeMillis());
        log.info("GM设置时间: {} {}", player, MyClock.nowString());
    }

    @GM(group = "系统设置", name = "自然跨天", level = 999, param = "无参数，设置成23:59:50")
    public void dayEnd(Player player, JSONArray args) {
        long time = MyClock.dayOfEndMillis() - TimeUnit.SECONDS.toMillis(10);
        AssertUtil.isTrue(time > MyClock.millis(), "时间不允许回调");
        MyClock.TimeOffset.set(time - System.currentTimeMillis());
        log.info("GM设置时间: {} {}", player, MyClock.nowString());
    }

}
