package wxdgaming.game.server;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.core.BootstrapProperties;
import wxdgaming.boot2.core.timer.MyClock;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 游戏服务配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 10:41
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "boot")
public class GameServerProperties extends BootstrapProperties {

    private int serverType = 1;
    private String openTime = "2025-09-11 17:00:00";
    private String centerUrl = "http://127.0.0.1:19600";

    @JSONField(serialize = false, deserialize = false)
    @Setter(value = AccessLevel.PRIVATE)
    @Getter(value = AccessLevel.PRIVATE)
    private transient LocalDateTime openTimeLocalDateTime;

    public LocalDateTime openTimeLocalDateTime() {
        if (openTimeLocalDateTime == null) {
            Date date = MyClock.parseDate(MyClock.SDF_YYYYMMDDHHMMSS_2, openTime);
            openTimeLocalDateTime = MyClock.localDateTime(date.getTime());
        }
        return openTimeLocalDateTime;
    }

    /** 从1 开始，开服当天是第一天 */
    public int openDay() {
        return MyClock.countDays(openTimeLocalDateTime(), MyClock.localDateTime()) + 1;
    }

}
