package wxdgaming.boot2.starter.condition;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.event.InitEvent;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 条件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-26 15:03
 **/
@Slf4j
@Service
public class ConditionService {

    Map<String, AbstractConditionProcessor> conditionProcessorMap;
    Map<String, IConditionComparator> conditionComparatorMap;

    @EventListener
    public void init(InitEvent event) {
        ApplicationContextProvider applicationContextProvider = event.applicationContextProvider();
        conditionProcessorMap = applicationContextProvider.toMap(AbstractConditionProcessor.class, v -> v.conditionKey().toUpperCase());
        conditionComparatorMap = applicationContextProvider.toMap(IConditionComparator.class, v -> v.compareKey().toUpperCase());
    }

    public List<Condition> parse(String conditionCfg) {
        if (StringUtils.isBlank(conditionCfg)) {
            return Collections.emptyList();
        }
        List<Condition> conditions = new ArrayList<>();
        String[] split = conditionCfg.split("&");
        for (String s : split) {
            String[] strings = s.split("#");
            List<String> stringList = List.of(strings);
            JSONArray jsonArray = new JSONArray(stringList);
            String conditionKey = jsonArray.getString(0).toUpperCase();
            AbstractConditionProcessor processor = conditionProcessorMap.get(conditionKey);
            AssertUtil.isNull(processor, "条件不存在：{}", conditionKey);
            String compare = jsonArray.getString(1).toUpperCase();
            IConditionComparator comparator = conditionComparatorMap.get(compare);
            AssertUtil.isNull(comparator, "比较器不存在：{}", compare);
            Condition condition = new Condition(conditionKey, comparator, jsonArray);
            conditions.add(condition);
        }
        return conditions;
    }

    public boolean testAll(JSONObject self, List<Condition> conditions, Consumer<String> errorConsumer) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        for (Condition condition : conditions) {
            String processorKey = condition.processorKey();
            AbstractConditionProcessor processor = conditionProcessorMap.get(processorKey);
            long selfValue = processor.selfValue(self, condition);
            if (!condition.test(selfValue)) {
                String tips = processor.tips(self, condition);
                log.debug("条件检查验证不通过: {} {}", self, tips);
                if (errorConsumer != null) {
                    errorConsumer.accept(tips);
                }
                return false;
            }
        }
        return true;
    }


}
