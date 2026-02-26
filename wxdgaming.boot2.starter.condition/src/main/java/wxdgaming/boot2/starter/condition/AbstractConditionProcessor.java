package wxdgaming.boot2.starter.condition;

import wxdgaming.boot2.core.InitPrint;

/**
 * 条件处理器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-26 15:48
 **/
public abstract class AbstractConditionProcessor<T extends ConditionDTO> implements InitPrint {

    public abstract String conditionKey();

    public abstract String tips(T self, Condition condition);

    public abstract long selfValue(T self, Condition condition);

}
