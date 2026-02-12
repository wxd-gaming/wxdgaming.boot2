package wxdgaming.game.server.script.date;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.date.AbstractDateConvert;
import wxdgaming.game.server.GameServerProperties;

import java.util.Arrays;

/**
 * 开服时间转换
 * <p>OpenServerTime&minute#30 开服时间当天凌晨计算持续30分钟
 * <p>OpenServerTime#day#1&minute#30 开服第二天凌晨开始持续半小时
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-12 14:19
 **/
@Component
public class OpenServerTimeMinConvertImpl extends AbstractDateConvert implements InitPrint {

    final GameServerProperties gameServerProperties;

    @Autowired
    public OpenServerTimeMinConvertImpl(GameServerProperties gameServerProperties) {
        this.gameServerProperties = gameServerProperties;
    }

    @Override public String type() {
        return "OpenServerTimeMin";
    }

    @Override public long convert(JSONObject extendParams, String[] params) {
        long openTimeToMillis = gameServerProperties.openTimeToMillis();
        long add = 0;
        if (params.length > 3) {
            String[] newParams = Arrays.copyOfRange(params, 1, params.length - 1);
            add = dateService.convert(extendParams, newParams);
        }
        long target = openTimeToMillis + add;
        target = MyClock.dayMinTime(target);
        return target;
    }

    @Override public long convertEndTime(JSONObject extendParams, long startTime, String[] params) {
        return convert(extendParams, params) - 1000;
    }

}
