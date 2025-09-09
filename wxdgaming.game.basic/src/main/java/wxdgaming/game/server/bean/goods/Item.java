package wxdgaming.game.server.bean.goods;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectLong;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.cfg.QItemTable;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.message.bag.ItemBean;

/**
 * 道具
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-21 19:44
 **/
@Getter
@Setter
public class Item extends ObjectLong {

    private int cfgId;
    private boolean bind;
    private long count;
    private long createTime;
    /** 过期时间 */
    private long expireTime;
    private JSONObject otherData = new JSONObject();

    /** 转化成通信消息类 */
    public ItemBean toItemBean() {
        ItemBean itemBean = new ItemBean();
        itemBean.setUid(getUid());
        itemBean.setItemId(getCfgId());
        itemBean.setBind(isBind());
        itemBean.setCount(getCount());
        itemBean.setExpireTime(getExpireTime());
        return itemBean;
    }

    public QItem qItem() {
        return DataRepository.getIns().dataTable(QItemTable.class, getCfgId());
    }

    public String toName() {
        QItem qItem = qItem();
        return "%s(%s, %s)".formatted(getUid(), qItem.getId(), qItem.getName());
    }

    @Override public String toString() {
        return toName();
    }
}
