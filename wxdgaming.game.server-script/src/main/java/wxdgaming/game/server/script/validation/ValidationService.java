package wxdgaming.game.server.script.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.executor.StackUtils;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.List;
import java.util.Map;

/**
 * 验证 validation
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 11:20
 **/
@Slf4j
@Service
public class ValidationService extends HoldApplicationContext {

    Map<ConditionType, AbstractValidationHandler> validationHandlerMap;
    final TipsService tipsService;

    public ValidationService(TipsService tipsService) {
        this.tipsService = tipsService;
    }

    @Init
    public void init() {
        validationHandlerMap = getApplicationContextProvider().toMap(AbstractValidationHandler.class, AbstractValidationHandler::conditionType);
    }

    public boolean validate(Player player, ConfigString configString, boolean sendTips) {
        if (configString == null || StringUtils.isBlank(configString.getValue())) {
            return true;
        }
        /*TODO 参考格式 1,1,1;2,1,1*/
        List<long[]> longList = configString.longArrayList();
        for (long[] longs : longList) {
            int ct = (int) longs[0];
            ConditionType conditionType = ConditionType.of(ct);
            if (conditionType == null) {
                log.warn("验证条件类型不存在: {}, {}", ct, StackUtils.stackAll());
                if (sendTips) {
                    tipsService.tips(player, "服务器异常");
                }
                return false;
            }
            AbstractValidationHandler validationHandler = validationHandlerMap.get(conditionType);
            if (validationHandler == null) {
                log.warn("验证条件为实现: {}, {}", configString.getValue(), StackUtils.stackAll());
                if (sendTips) {
                    tipsService.tips(player, "服务器异常");
                }
                return false;
            }
            if (!validationHandler.validate(player, longs)) {
                log.debug("{} 验证条件失败: {} - {}", player, conditionType, configString.getValue());
                if (sendTips) {
                    tipsService.tips(player, validationHandler.tips());
                }
                return false;
            }
        }
        return true;
    }

}
