package wxdgaming.game.login.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 使用礼包码
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-09 13:57
 **/
@Getter
@Setter
public class UseGiftCodeDTO extends ObjectBase {

    private String giftCode;
    private int sid;
    private String account;
    private long roleId;
    private String roleName;

}
