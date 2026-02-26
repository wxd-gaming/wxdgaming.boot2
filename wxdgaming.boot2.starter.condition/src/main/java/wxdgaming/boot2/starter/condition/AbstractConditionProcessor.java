package wxdgaming.boot2.starter.condition;

import com.alibaba.fastjson2.JSONObject;
import wxdgaming.boot2.core.InitPrint;

/**
 * 条件处理器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-26 15:48
 **/
public abstract class AbstractConditionProcessor implements InitPrint {

    public abstract String conditionKey();

    public abstract String tips(JSONObject self, Condition condition);

    public abstract long selfValue(JSONObject self, Condition condition);

}
