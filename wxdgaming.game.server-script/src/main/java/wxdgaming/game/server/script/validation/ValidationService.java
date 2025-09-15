package wxdgaming.game.server.script.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.executor.StackUtils;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.boot2.starter.validation.AbstractValidationHandler;
import wxdgaming.boot2.starter.validation.Validation;
import wxdgaming.boot2.starter.validation.ValidationType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 验证 validation
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 11:20
 **/
@Slf4j
@Service
public class ValidationService extends HoldApplicationContext {

    Map<ValidationType, AbstractValidationHandler> validationHandlerMap;
    final TipsService tipsService;

    public ValidationService(TipsService tipsService) {
        this.tipsService = tipsService;
    }

    @Init
    @Order(1)
    public void init() {
        validationHandlerMap = getApplicationContextProvider().toMap(AbstractValidationHandler.class, AbstractValidationHandler::conditionType);
    }

    /** 完全满足条件 */
    public boolean validateAll(Object object, ConfigString configString, boolean sendTips) {
        return validateAll(object, configString, Validation.Parse, sendTips);
    }

    /** 完全满足条件 */
    public boolean validateAll(Object object, ConfigString configString, Function<String, List<Validation>> parse, boolean sendTips) {
        if (configString == null || StringUtils.isBlank(configString.getValue())) {
            return true;
        }
        /*TODO 参考格式 1,1,1;2,1,1*/
        List<Validation> validations = configString.get(parse);
        for (Validation validation : validations) {
            AbstractValidationHandler<Object> validationHandler = validationHandlerMap.get(validation.getValidationType());
            if (validationHandler == null) {
                log.warn("验证条件为实现: {}, {}", configString.getValue(), StackUtils.stackAll());
                if (sendTips && object instanceof Player player) {
                    tipsService.tips(player, "服务器异常");
                }
                return false;
            }
            if (!validationHandler.validate(object, validation)) {
                log.debug("{} 验证条件失败: {}", object, validation);
                if (sendTips && object instanceof Player player) {
                    tipsService.tips(player, validationHandler.tips());
                }
                return false;
            }
        }
        return true;
    }

    /** 完全满足条件 */
    public boolean validateAny(Object player, ConfigString configString, boolean sendTips) {
        return validateAny(player, configString, Validation.Parse, sendTips);
    }

    /** 任意一个条件满足就行 */
    public boolean validateAny(Object object, ConfigString configString, Function<String, List<Validation>> parse, boolean sendTips) {
        if (configString == null || StringUtils.isBlank(configString.getValue())) {
            return true;
        }
        /*TODO 参考格式 1,1,1;2,1,1*/
        List<Validation> validations = configString.get(parse);
        for (Validation validation : validations) {
            AbstractValidationHandler<Object> validationHandler = validationHandlerMap.get(validation.getValidationType());
            if (validationHandler == null) {
                log.warn("验证条件为实现: {}, {}", configString.getValue(), StackUtils.stackAll());
                if (sendTips && object instanceof Player player) {
                    tipsService.tips(player, "服务器异常");
                }
                return false;
            }
            if (validationHandler.validate(object, validation)) {
                return true;
            }
        }
        return false;
    }


}
