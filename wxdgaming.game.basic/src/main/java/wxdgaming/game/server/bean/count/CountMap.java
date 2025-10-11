package wxdgaming.game.server.bean.count;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-10 21:05
 **/
@Getter
@Setter
public class CountMap extends ObjectBase {

    private Map<CountValidationType, CountData> validationMap = new HashMap<>();

    public void checkClear() {
        for (Map.Entry<CountValidationType, CountData> entry : validationMap.entrySet()) {
            CountValidationType validationType = entry.getKey();
            CountData countData = entry.getValue();
            countData.checkClear(validationType.getCheck());
        }
    }

}
