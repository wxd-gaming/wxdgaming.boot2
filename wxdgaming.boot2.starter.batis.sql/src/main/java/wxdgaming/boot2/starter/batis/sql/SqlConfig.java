package wxdgaming.boot2.starter.batis.sql;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 数据库配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:43
 **/
@Getter
@Setter
public class SqlConfig extends ObjectBase {

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private int minPoolSize;
    private int maxPoolSize;

}
