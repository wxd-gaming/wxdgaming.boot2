package wxdgaming.boot2.starter.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.executor.StackUtils;
import wxdgaming.boot2.core.lang.ConfigString;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 验证
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-15 09:03
 **/
@Slf4j
@Component
public class ValidationUtil extends HoldApplicationContext {

    Map<ValidationType, AbstractValidationHandler> validationHandlerMap;

    @Init
    @Order(1)
    public void init() {
        validationHandlerMap = getApplicationContextProvider().toMap(AbstractValidationHandler.class, AbstractValidationHandler::conditionType);
    }

    /** 完全满足条件 */
    public boolean validateAll(Object object, ConfigString configString, Consumer<String> errorCall) {
        return validateAll(object, configString, Validation.Parse, errorCall);
    }

    /** 完全满足条件 */
    @SuppressWarnings("unchecked")
    public boolean validateAll(Object object, ConfigString configString, Function<String, List<Validation>> parse, Consumer<String> errorCall) {
        if (configString == null || StringUtils.isBlank(configString.getValue())) {
            return true;
        }
        /*TODO 参考格式 1,1,1;2,1,1*/
        List<Validation> validations = configString.get(parse);
        for (Validation validation : validations) {
            AbstractValidationHandler<Object> validationHandler = validationHandlerMap.get(validation.getValidationType());
            if (validationHandler == null) {
                log.warn("验证条件为实现: {}, {}", configString.getValue(), StackUtils.stackAll());
                return false;
            }
            if (!validationHandler.validate(object, validation)) {
                log.debug("{} 验证条件失败: {}", object, validation);
                if (errorCall != null) {
                    errorCall.accept(validationHandler.tips());
                }
                return false;
            }
        }
        return true;
    }

    /** 完全满足条件 */
    public boolean validateAny(Object player, ConfigString configString) {
        return validateAny(player, configString, Validation.Parse);
    }

    /** 任意一个条件满足就行 */
    @SuppressWarnings("unchecked")
    public boolean validateAny(Object object, ConfigString configString, Function<String, List<Validation>> parse) {
        if (configString == null || StringUtils.isBlank(configString.getValue())) {
            return true;
        }
        /*TODO 参考格式 1,1,1;2,1,1*/
        List<Validation> validations = configString.get(parse);
        for (Validation validation : validations) {
            AbstractValidationHandler<Object> validationHandler = validationHandlerMap.get(validation.getValidationType());
            if (validationHandler == null) {
                log.warn("验证条件为实现: {}, {}", configString.getValue(), StackUtils.stackAll());
                return false;
            }
            if (validationHandler.validate(object, validation)) {
                return true;
            }
        }
        return false;
    }

}
