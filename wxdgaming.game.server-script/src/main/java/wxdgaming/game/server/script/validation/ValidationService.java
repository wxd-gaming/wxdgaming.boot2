package wxdgaming.game.server.script.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.boot2.starter.validation.AbstractValidationHandler;
import wxdgaming.boot2.starter.validation.Validation;
import wxdgaming.boot2.starter.validation.ValidationUtil;
import wxdgaming.game.server.bean.ValidationType;
import wxdgaming.game.server.bean.count.CountValidationType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.List;
import java.util.function.BiConsumer;
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

    final ValidationUtil validationUtil;
    final TipsService tipsService;

    public ValidationService(ValidationUtil validationUtil, TipsService tipsService) {
        this.validationUtil = validationUtil;
        this.tipsService = tipsService;
    }

    /** 完全满足条件 */
    public boolean validateCountAll(Object object, ConfigString configString, boolean sendTips) {
        return validateAll(object, configString, CountValidationType.Parse, sendTips);
    }

    /** 完全满足条件 */
    public boolean validateAll(Object object, ConfigString configString, boolean sendTips) {
        return validateAll(object, configString, ValidationType.Parse, sendTips);
    }

    /** 完全满足条件 */
    public boolean validateAll(Object object, ConfigString configString, Function<String, List<Validation>> parse, boolean sendTips) {
        if (configString == null || StringUtils.isBlank(configString.getValue())) {
            return true;
        }
        /*TODO 参考格式 1,1,1;2,1,1*/
        List<Validation> validations = configString.get(parse);
        return validateAll(object, validations, sendTips);
    }

    public boolean validateAll(Object object, List<Validation> validations, boolean sendTips) {
        return validateAll(object, validations, (handler, validation) -> {
            if (sendTips && object instanceof Player player) {
                tipsService.tips(player, handler.tips());
            }
        });
    }

    public boolean validateAll(Object object, List<Validation> validations, BiConsumer<AbstractValidationHandler<Object>, Validation> errorCall) {
        return validationUtil.validateAll(object, validations, errorCall);
    }

    /** 完全满足条件 */
    public boolean validateAny(Object object, ConfigString configString) {
        return validateAny(object, configString, ValidationType.Parse);
    }

    /** 任意一个条件满足就行 */
    public boolean validateAny(Object object, ConfigString configString, Function<String, List<Validation>> parse) {
        if (configString == null || StringUtils.isBlank(configString.getValue())) {
            return true;
        }
        /*TODO 参考格式 1,1,1;2,1,1*/
        List<Validation> validations = configString.get(parse);
        return validateAny(object, validations);
    }

    public boolean validateAny(Object object, List<Validation> validations) {
        return validationUtil.validateAny(object, validations, (handler) -> {

        });
    }
}
