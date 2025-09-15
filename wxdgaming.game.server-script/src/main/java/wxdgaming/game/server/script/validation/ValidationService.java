package wxdgaming.game.server.script.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.boot2.starter.validation.Validation;
import wxdgaming.boot2.starter.validation.ValidationUtil;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.List;
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
    public boolean validateAll(Object object, ConfigString configString, boolean sendTips) {
        return validateAll(object, configString, Validation.Parse, sendTips);
    }

    /** 完全满足条件 */
    public boolean validateAll(Object object, ConfigString configString, Function<String, List<Validation>> parse, boolean sendTips) {
        return validationUtil.validateAll(object, configString, parse, (error) -> {
            if (sendTips && object instanceof Player player) {
                tipsService.tips(player, error);
            }
        });
    }

    /** 完全满足条件 */
    public boolean validateAny(Object player, ConfigString configString) {
        return validateAny(player, configString, Validation.Parse);
    }

    /** 任意一个条件满足就行 */
    public boolean validateAny(Object object, ConfigString configString, Function<String, List<Validation>> parse) {
        return validationUtil.validateAny(object, configString, parse);
    }


}
