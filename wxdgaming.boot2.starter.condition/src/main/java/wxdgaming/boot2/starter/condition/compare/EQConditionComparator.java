package wxdgaming.boot2.starter.condition.compare;

import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.condition.IConditionComparator;

/**
 * ==
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-26 15:59
 **/
@Component
public class EQConditionComparator implements IConditionComparator {

    @Override public String compareKey() {
        return "==";
    }

    @Override public boolean compare(long self, long target) {
        return self == target;
    }

}
