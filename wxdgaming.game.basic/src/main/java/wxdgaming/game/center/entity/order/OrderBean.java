package wxdgaming.game.center.entity.order;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

/**
 * 充值表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-15 15:34
 **/
@Getter
@Setter
@DbTable(tableComment = "充值表")
public class OrderBean extends Entity {

    @DbColumn(key = true)
    @JSONField(ordinal = -9999)
    private String orderId;
    @DbColumn(index = true, comment = "创建订单的时间")
    private long createTime;
    @DbColumn(index = true)
    private int gid;
    @DbColumn(index = true)
    private int sid;
    /** 区服名字 */
    private String sName;
    @DbColumn(index = true)
    private String loginName;
    @DbColumn(index = true)
    private long roleId;
    @DbColumn(index = true)
    private String roleName;
    @DbColumn(index = true)
    private String productID;
    @DbColumn(index = true)
    private String productName;
    /** 单位用分 */
    @DbColumn(index = true)
    private int productMoney;

    /** 平台订单号 */
    @DbColumn(index = true, comment = "商户订单号")
    private String spOrderId;
    /** 平台传递的金额，存储也要存分，如果平台传递是元，记得换算 */
    @DbColumn(index = true)
    private int spProductMoney;
    @DbColumn(index = true, comment = "平台创建订单的时间")
    private long spCreateTime;
    /** 状态 0表示拉订单成功, 1，表示回调成功, 2表示发货成功 */
    private int status;

}
