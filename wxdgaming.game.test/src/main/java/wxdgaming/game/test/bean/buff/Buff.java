package wxdgaming.game.test.bean.buff;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.timer.MyClock;

/**
 * 场景对象身上的buff
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 10:56
 **/
@Getter
@Setter
@Accessors(chain = true)
public class Buff extends ObjectBase {

    private long uid;
    private int buffId;
    private int level;
    private long startTime;
    private long endTime;
    private int interval;

    @JSONField(serialize = false, deserialize = false)
    public boolean checkStart() {
        return MyClock.millis() > startTime;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean checkEnd() {
        return MyClock.millis() > endTime;
    }

}
