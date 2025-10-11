package wxdgaming.game.server.script.validation.impl.count;

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
public abstract class AbstractCountValidationHandler extends AbstractValidationHandler<CountMap> {

    public abstract CountValidationType getValidationType();

    @Override public IValidationType conditionType() {
        return getValidationType();
    }

    @Override public String tips() {
        return "次数已达上限";
    }

    @Override public boolean validate(CountMap map, Validation validation) {
        CountData countData = map.getValidationMap().computeIfAbsent(getValidationType(), l -> new CountData());
        countData.checkClear(getValidationType().getCheck());
        return validation.test(countData.getCount());
    }


}
