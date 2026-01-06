package wxdgaming.logserver.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.function.Function;

/**
 * 日志自动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 19:50
 **/
@Getter
@Setter
@Accessors(chain = true)
public class LogField extends ObjectBase {

    private String fieldName;
    private String fieldComment;
    private String fieldType;
    private String fieldHtmlStyle;
    private String fieldHtmlTips;

    @JSONField(serialize = false, deserialize = false)
    private transient Function<String, Object> fieldValueFunction;

    public Function<String, Object> getFieldValueFunction() {
        if (fieldValueFunction == null) {
            fieldValueFunction = switch (getFieldType()) {
                case "int" -> Integer::valueOf;
                case "long" -> Long::valueOf;
                case "float" -> Float::valueOf;
                case "double" -> Double::valueOf;
                case "boolean" -> Boolean::valueOf;
                default -> String::valueOf;
            };
        }
        return fieldValueFunction;
    }
}
