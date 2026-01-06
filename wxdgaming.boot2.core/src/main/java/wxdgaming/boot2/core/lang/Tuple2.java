package wxdgaming.boot2.core.lang;


import com.alibaba.fastjson2.annotation.JSONCreator;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2020-11-18 13:46
 **/
@Getter
@Setter
public class Tuple2<L, R> implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    protected L left;
    protected R right;

    @JSONCreator
    public Tuple2(@JSONField(name = "left") L left,
                  @JSONField(name = "right") R right) {
        this.left = left;
        this.right = right;
    }

    @Override public String toString() {
        return "{left=%s, right=%s}".formatted(left, right);
    }
}
