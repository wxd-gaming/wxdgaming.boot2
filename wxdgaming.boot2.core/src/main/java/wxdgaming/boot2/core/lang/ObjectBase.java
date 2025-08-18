package wxdgaming.boot2.core.lang;

import com.alibaba.fastjson.JSONObject;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

import java.io.Serial;
import java.io.Serializable;

/**
 * 基类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 14:21
 **/
public abstract class ObjectBase implements Serializable, Cloneable {

    @Serial private static final long serialVersionUID = 1L;


    @Override public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject toJSONObject() {
        return FastJsonUtil.parseJSONObject(this);
    }

    public String toJSONString() {
        return FastJsonUtil.toJSONString(this);
    }

    public String toJSONStringAsFmt() {
        return FastJsonUtil.toJSONStringAsFmt(this);
    }

    public String toJSONStringAsWriteType() {
        return FastJsonUtil.toJSONStringAsWriteType(this);
    }

    @Override public String toString() {
        return FastJsonUtil.toJSONString(this);
    }
}
