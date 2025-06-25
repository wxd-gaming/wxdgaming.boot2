package update;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.format.TimeFormat;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * Fight 事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-25 17:01
 **/
@Getter
@Setter
@Accessors(chain = true)
public class FightEvent extends ObjectBase {

    private long times;
    private long attackRoleId;
    private long targetRoleId;

    private int skillId;
    private int skillLV;
    private long costHp;
    private long targetHp;
    private boolean targetDie;

    @Override public String toString() {
        return "FightEvent{times=%s, attackRoleId=%d, targetRoleId=%d, skillId=%d, skillLV=%d, costHp=%d, targetHp=%s, targetDie=%s}"
                .formatted(TimeFormat.of(times * 100), attackRoleId, targetRoleId, skillId, skillLV, costHp, targetHp, targetDie);
    }
}
