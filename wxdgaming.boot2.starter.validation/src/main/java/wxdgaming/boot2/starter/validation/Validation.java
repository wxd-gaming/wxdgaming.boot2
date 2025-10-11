package wxdgaming.boot2.starter.validation;

import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 条件配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 11:03
 */
@Getter
public class Validation extends ObjectBase {

    private final IValidationType validationType;
    private final ValidationEquals validationEquals;
    private final long value;

    public Validation(IValidationType validationType, ValidationEquals validationEquals, long value) {
        this.validationType = validationType;
        this.validationEquals = validationEquals;
        this.value = value;
    }

    public boolean test(long target) {
        return validationEquals.getPredicate().test(target, this.value);
    }

    @Override public String toString() {
        return "Validation{%s %s %d}".formatted(validationType.toString(), validationEquals.getCodeString(), value);
    }
}
