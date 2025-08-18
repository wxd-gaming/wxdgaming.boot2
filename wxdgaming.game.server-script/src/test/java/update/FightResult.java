package update;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 战斗结果
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-25 17:00
 **/
@Getter
@Setter
public class FightResult extends ObjectBase {

    private long times;
    private List<FightEvent> fightEventList = new ArrayList<>();

}
