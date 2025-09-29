package wxdgaming.game.login.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

import java.util.Collections;
import java.util.List;

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
    private String uid;
    @DbColumn(index = true, length = 64)
    private String userName;
    @DbColumn(index = true, length = 128)
    private String password;
    @DbColumn(index = true, length = 16)
    private String phone;
    /** 是否是管理员 */
    @DbColumn(index = true)
    private boolean admin;
    private int lv = 0;
    private long loginCount;
    /** 路由权限 */
    @DbColumn(columnType = ColumnType.String, length = 12000)
    private List<String> routes = Collections.emptyList();
}
