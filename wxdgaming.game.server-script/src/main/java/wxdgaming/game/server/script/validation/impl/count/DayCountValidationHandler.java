package wxdgaming.game.server.script.validation.impl.count;

import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.validation.AbstractValidationHandler;
import wxdgaming.boot2.starter.validation.IValidationType;
import wxdgaming.boot2.starter.validation.Validation;
import wxdgaming.game.server.bean.count.CountData;
import wxdgaming.game.server.bean.count.CountMap;
import wxdgaming.game.server.bean.count.CountValidationType;

/**
 * 等级验证 条件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 10:55
 **/
@Component
public class DayCountValidationHandler extends AbstractValidationHandler<CountMap> {

    public CountValidationType getValidationType() {
        return CountValidationType.DayCount;
    }

    @Override public IValidationType conditionType() {
        return getValidationType();
    }

    @Override public String tips() {
        return "次数已达上限";
    }

    @Override public boolean validate(CountMap map, Validation validation) {
        CountData countData = map.getValidationMap().get(getValidationType());
        if (countData == null) return true;
        countData.checkClear(getValidationType().getCheck());
        return countData.getCount() == validation.getValue();
    }


}
