package wxdgaming.boot2.core.lang;


import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2020-11-18 13:46
 **/
@Getter
@Setter
public class TupleIntLong implements Serializable {

    protected int left;
    protected long right;

    @JSONCreator
    public TupleIntLong(@JSONField(name = "left") int left, @JSONField(name = "right") long right) {
        this.left = left;
        this.right = right;
    }

    @Override public String toString() {
        return "{left=%d, right=%d}".formatted(left, right);
    }
}
