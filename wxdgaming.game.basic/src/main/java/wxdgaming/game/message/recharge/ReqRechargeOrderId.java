package  wxdgaming.game.message.recharge;

import io.protostuff.Tag;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.ann.Comment;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.starter.net.pojo.PojoBase;


/** 请求下单 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("请求下单")
public class ReqRechargeOrderId extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 58256455;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 商品id */
    @Tag(1) private int productID;
    /** 购买数量 */
    @Tag(2) private int count;


}
