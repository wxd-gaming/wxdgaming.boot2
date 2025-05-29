package wxdgaming.boot2.starter.net.pojo;


import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * protobuf 映射 基类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-05-28 21:24
 **/
public abstract class PojoBase extends ObjectBase {

    public int msgId() {
        return 0;
    }

    /** 编码 */
    public byte[] encode() {
        return SerializerUtil.encode(this);
    }

    /** 解码 */
    public void decode(byte[] bytes) {
        SerializerUtil.decode(bytes, this);
    }

    @Override public String toString() {
        return this.getClass().getSimpleName() + FastJsonUtil.toJSONString(this);
    }

}
