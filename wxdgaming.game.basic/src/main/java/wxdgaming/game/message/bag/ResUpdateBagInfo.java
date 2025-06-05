package  wxdgaming.game.message.bag;

import io.protostuff.Tag;
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


/** 响应背包信息 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("响应背包信息")
public class ResUpdateBagInfo extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 49916070;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private BagType bagType;
    /** 所有的货币 */
    @Tag(2) private Map<Integer, Long> currencyMap = new LinkedHashMap<>();
    /** 所有的物品 */
    @Tag(3) private List<ItemBean> items = new ArrayList<>();
    /** 更新原因 */
    @Tag(4) private String reason;
    /** 其他信息-与客户端协商 */
    @Tag(7) private String other;


}
