package wxdgaming.game.server.script.validation;

import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.List;
import java.util.function.Function;

/**
 * 条件配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 11:03
 */
@Getter
public class Validation extends ObjectBase {

    public static final Function<String, List<Validation>> Parse = (string) -> {
        String[] split = string.split(";");
        List<Validation> list = new java.util.ArrayList<>();
        for (String s : split) {
            String[] vs = s.split("[|]");
            Validation validation = new Validation(ValidationType.valueOf(vs[0]), ValidationEquals.valueOf(vs[1]), Long.parseLong(vs[2]));
            list.add(validation);
        }
        return list;
    };

    public static final Function<String, List<Validation>> Parse2 = (string) -> {
        String[] split = string.split(";");
        List<Validation> list = new java.util.ArrayList<>();
        for (String s : split) {
            String[] vs = s.split("[|]");
            Validation validation = new Validation(ValidationType.valueOf(vs[0]), ValidationEquals.of2OrException(vs[1]), Long.parseLong(vs[2]));
            list.add(validation);
        }
        return list;
    };

    private final ValidationType validationType;
    private final ValidationEquals validationEquals;
    private final long value;

    public Validation(ValidationType validationType, ValidationEquals validationEquals, long value) {
        this.validationType = validationType;
        this.validationEquals = validationEquals;
        this.value = value;
    }

    public boolean test(long target) {
        return validationEquals.getPredicate().test(target, this.value);
    }

    @Override public String toString() {
        return "Validation{%s, %s %d}".formatted(validationType.getComment(), validationEquals.getComment(), value);
    }
}
