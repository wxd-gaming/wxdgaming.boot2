package wxdgaming.boot2.core.lang;


import com.alibaba.fastjson2.annotation.JSONCreator;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2020-11-18 13:46
 **/
@Getter
@Setter
public class TupleLong implements Serializable {

    protected long left;
    protected long right;

    @JSONCreator
    public TupleLong(@JSONField(name = "left") long left, @JSONField(name = "right") long right) {
        this.left = left;
        this.right = right;
    }

    @Override public String toString() {
        return "{left=%d, right=%d}".formatted(left, right);
    }
}
