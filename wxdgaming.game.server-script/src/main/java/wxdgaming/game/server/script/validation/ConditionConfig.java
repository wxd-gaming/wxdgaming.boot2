package wxdgaming.game.server.script.validation;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.function.Function;

/**
 * 条件配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 11:03
 */
@Getter
public class ConditionConfig extends ObjectBase {

    @Getter @JSONField(ordinal = 1)
    private final String value;
    private transient String key;
    private transient String condition;
    private transient Object object = null;

    @JSONCreator()
    public ConditionConfig(@JSONField(name = "value") String value) {
        this.value = value;
        int i = this.value.indexOf("|");
        this.key = this.value.substring(0, i);
        this.condition = this.value.substring(i + 1);
    }

    /** 自定义转化，避免每次都转化 */
    @SuppressWarnings("unchecked")
    public <T> T get(Function<String, T> function) {
        if (StringUtils.isBlank(condition)) return null;
        if (object == null) {
            object = function.apply(condition);
        }
        return (T) getObject();
    }

    @Override public String toString() {
        return value;
    }
}
