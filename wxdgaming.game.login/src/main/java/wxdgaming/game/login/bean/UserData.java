package wxdgaming.game.login.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

/**
 * 登录数据
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:29
 **/
@Getter
@Setter
@DbTable
public class UserData extends Entity {

    @DbColumn(key = true, length = 64)
    private String account;
    /** 登录密钥，如果是渠道sdk这个其实无意义 */
    @DbColumn(length = 64)
    private String token;
    private long createTime;
    @DbColumn(index = true, length = 64)
    private String platform;
    /** 平台返回的userid */
    @DbColumn(index = true, length = 64)
    private String platformUserId;


}
