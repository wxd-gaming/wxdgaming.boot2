package wxdgaming.game.bean.vip;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * vip
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-12 13:16
 **/
@Getter
@Setter
public class VipInfo {

    private int lv;
    private long exp;
    /** 激活vip的时间 */
    private long activatedTime;
    /** 已经领取过的免费奖励 */
    private ArrayList<Integer> rewardIdList = new ArrayList<>();
    /** 购买过的奖励 */
    private ArrayList<Integer> buyRewardIdList = new ArrayList<>();
}
