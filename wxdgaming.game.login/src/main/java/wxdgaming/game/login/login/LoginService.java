package wxdgaming.game.login.login;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.format.HexId;
import wxdgaming.boot2.starter.batis.sql.SqlDataCache;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.game.login.LoginServerProperties;
import wxdgaming.game.login.entity.UserData;

/**
 * 登录服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-07 18:37
 **/
@Slf4j
@Getter
@Service
public class LoginService {

    final HexId userHexId;
    final SqlDataHelper sqlDataHelper;

    public LoginService(LoginServerProperties loginServerProperties, SqlDataHelper sqlDataHelper) {
        this.userHexId = new HexId(loginServerProperties.getSid());
        this.sqlDataHelper = sqlDataHelper;
    }

    public UserData userData(String userName) {
        SqlDataCache<UserData, String> cached = sqlDataHelper.getCacheService().cache(UserData.class);
        return cached.getIfPresent(userName);
    }

}
