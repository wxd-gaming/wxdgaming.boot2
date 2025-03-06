package wxdgaming.entity.bean.mapping;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.excel.store.DataKey;
import wxdgaming.boot2.starter.excel.store.DataMapping;

import java.io.Serializable;
import java.util.*;

/**
 * excel 构建 vip礼包, src/main/resources/范例.xlsx, q_itemshop_vip,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DataMapping(name = "q_itemshop_vip", comment = "vip礼包", excelPath = "src/main/resources/范例.xlsx", sheetName = "q_itemshop_vip")
public abstract class QItemshopVipMapping extends ObjectBase implements Serializable, DataKey {

    /** 主键id */
    protected int id;
    /** 商品内容 */
    protected wxdgaming.boot2.core.lang.ConfigString shop_item;
    /** 如果数据库关联字符串超长 */
    protected String name_1;
    /** 客户端使用字段 */
    protected String gift_name;
    /** 服务器使用字段 */
    protected int show_viplv;
    /** cron表达式 */
    protected wxdgaming.boot2.core.timer.CronExpress show_time;
    /** 是非类型 */
    protected boolean conditions_viplv;
    /** 小数类型 */
    protected float limit_num;
    /** 售价 */
    protected int[] price;

    public Object key() {
        return id;
    }

}
