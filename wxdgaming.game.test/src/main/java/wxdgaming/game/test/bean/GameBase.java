package wxdgaming.game.test.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.EntityLongUID;

/**
 * 基类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-15 17:14
 **/
@Getter
@Setter
public class GameBase extends EntityLongUID {

    private long createTime;

}
