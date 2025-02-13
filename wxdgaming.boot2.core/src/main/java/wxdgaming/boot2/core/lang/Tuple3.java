package wxdgaming.boot2.core.lang;


import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2020-11-18 13:46
 **/
@Getter
@Setter
public class Tuple3<L, C, R> implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    protected L left;
    protected C center;
    protected R right;

    @JSONCreator
    public Tuple3(@JSONField(name = "left") L left,
                  @JSONField(name = "center") C center,
                  @JSONField(name = "right") R right) {
        this.left = left;
        this.center = center;
        this.right = right;
    }

    @Override public String toString() {
        return "{left=%s, center=%s, right=%s}".formatted(left, center, right);
    }
}
