package wxdgaming.game.server.script.validation.impl.count;

import org.springframework.stereotype.Component;
import wxdgaming.game.server.bean.count.CountValidationType;

/**
 * 每年次数 条件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 10:55
 **/
@Component
public class YearCountValidationHandler extends AbstractCountValidationHandler {

    public CountValidationType getValidationType() {
        return CountValidationType.YearCount;
    }

    @Override public String tips() {
        return "每年次数已达上限";
    }
}
