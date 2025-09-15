package wxdgaming.boot2.starter.validation;

/**
 * 验证
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 10:53
 **/
public abstract class AbstractValidationHandler<T> {

    public abstract ValidationType conditionType();

    public abstract String tips();

    public abstract boolean validate(T t, Validation validation);

}
