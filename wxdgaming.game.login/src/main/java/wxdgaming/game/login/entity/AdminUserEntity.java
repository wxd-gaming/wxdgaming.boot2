package wxdgaming.game.login.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

/**
 * 管理用户
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-10 15:13
 **/
@Getter
@Setter
@DbTable
public class AdminUserEntity extends Entity {

    @DbColumn(key = true, length = 64)
    private String userName;
    @DbColumn(index = true, length = 128)
    private String password;

}
