package wxdgaming.boot2.starter.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.InitEvent;
import wxdgaming.boot2.core.executor.StackUtils;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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

    Map<IValidationType, AbstractValidationHandler> validationHandlerMap;

    @Order(-100)
    @EventListener
    public void init(InitEvent initEvent) {
        validationHandlerMap = getApplicationContextProvider().toMap(AbstractValidationHandler.class, AbstractValidationHandler::conditionType);
    }

    /** 完全满足条件 */
    public boolean validateAll(Object object, ConfigString configString, Function<String, List<Validation>> parse,
                               BiConsumer<AbstractValidationHandler<Object>, Validation> errorCall) {
        if (configString == null || StringUtils.isBlank(configString.getValue())) {
            return true;
        }
        /*TODO 参考格式 1,1,1;2,1,1*/
        List<Validation> validations = configString.get(parse);
        return validateAll(object, validations, errorCall);
    }

    @SuppressWarnings("unchecked")
    public boolean validateAll(Object object, List<Validation> validations, BiConsumer<AbstractValidationHandler<Object>, Validation> errorCall) {
        for (Validation validation : validations) {
            AbstractValidationHandler<Object> validationHandler = validationHandlerMap.get(validation.getValidationType());
            AssertUtil.isNull(validationHandler, "验证条件为实现: %s, %s", validation.getValidationType(), StackUtils.stackAll());
            if (!validationHandler.validate(object, validation)) {
                if (errorCall != null) {
                    errorCall.accept(validationHandler, validation);
                } else {
                    log.debug("验证条件失败: {} -> {}", validation, object);
                }
                return false;
            }
        }
        return true;
    }

    /** 任意一个条件满足就行 */

    public boolean validateAny(Object object, ConfigString configString, Function<String, List<Validation>> parse, Consumer<AbstractValidationHandler<Object>> sources) {
        if (configString == null || StringUtils.isBlank(configString.getValue())) {
            return true;
        }
        /*TODO 参考格式 1,1,1;2,1,1*/
        List<Validation> validations = configString.get(parse);
        return validateAny(object, validations, sources);
    }

    @SuppressWarnings("unchecked")
    public boolean validateAny(Object object, List<Validation> validations, Consumer<AbstractValidationHandler<Object>> sources) {
        for (Validation validation : validations) {
            AbstractValidationHandler<Object> validationHandler = validationHandlerMap.get(validation.getValidationType());
            AssertUtil.isNull(validationHandler, "验证条件为实现: %s, %s", validation.getValidationType(), StackUtils.stackAll());
            if (validationHandler.validate(object, validation)) {
                if (sources != null) {
                    sources.accept(validationHandler);
                } else {
                    log.debug("验证条件成功: {} -> {}", validation, object);
                }
                return true;
            }
        }
        return false;
    }

}
