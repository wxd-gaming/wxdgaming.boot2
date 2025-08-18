package wxdgaming.game.bean.mail;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 全服邮件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-12 15:30
 **/
@Getter
@Setter
@Accessors(chain = true)
public class ServerMailInfo extends MailInfo {

    private int lvMin;
    private int lvMax;
    private int vipLvMin;
    private int vipLvMax;
    /** 指定的角色可以领取 */
    private List<Long> ridList = new ArrayList<>();

    /** 已经领取过该邮件的角色 */
    private List<Long> rewardRidList = new ArrayList<>();


}
