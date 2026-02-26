package wxdgaming.boot2.starter.condition;

import com.alibaba.fastjson2.JSONArray;

/**
 * 完整的条件
 * <p> 如果有多个参数，第一个参数是目标值，第二个第三个自定义，比如要求属性力量大于1000，应该是配置：属性#>=#1000#力量
 *
 * @param values 目标参数
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-26 15:04
 */
public record Condition(String processorKey, IConditionComparator comparator, JSONArray values) {

    public long targetValue() {
        return values.getLongValue(2);
    }

    public boolean test(Long self) {
        long targetValue = values.getLongValue(2);
        return comparator.compare(self, targetValue);
    }

}
