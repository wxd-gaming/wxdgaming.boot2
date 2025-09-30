package wxdgaming.game.login.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-07 18:29
 **/
@Getter
@Setter
@DbTable
public class UserData extends Entity {

    @DbColumn(key = true, length = 64)
    private String account;
    /** 登录密钥，如果是渠道sdk这个其实无意义 */
    @DbColumn(length = 128)
    private String token;
    @DbColumn(index = true)
    private long createTime;
    @DbColumn(index = true)
    private int appId;
    @DbColumn(index = true, length = 64)
    private String platform;
    /** 平台返回的 Channel id */
    @DbColumn(index = true, length = 64)
    private String platformChannelId;
    /** 平台返回的userid */
    @DbColumn(index = true, length = 64)
    private String platformUserId;
    private long loginCount;
    private long lastLoginTime;
    /** 禁止登录的过期时间 */
    private long banExpireTime;
    /** 是不是白名单 */
    private boolean white;
    private int gmLevel;
    private int lastLoginServerId;
    private String lastLoginServerTime;
    @DbColumn(length = 20000, columnType = ColumnType.String)
    private ConcurrentHashMap<Integer, String> gameRoleMap = new ConcurrentHashMap<>();

}
