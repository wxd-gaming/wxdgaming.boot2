package wxdgaming.game.server.script.validation.impl.count;

import org.springframework.stereotype.Component;
import wxdgaming.game.server.bean.count.CountValidationType;

/**
 * 永久性次数限制 条件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 10:55
 **/
@Component
public class ForeverCountValidationHandler extends AbstractCountValidationHandler {

    public CountValidationType getValidationType() {
        return CountValidationType.ForeverCount;
    }

}
