package wxdgaming.game.login.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.format.HexId;
import wxdgaming.boot2.starter.batis.sql.SqlDataCache;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.game.login.bean.UserData;

/**
 * 登录服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:37
 **/
@Slf4j
@Getter
@Singleton
public class LoginService {

    HexId userHexId;
    final SqlDataHelper sqlDataHelper;

    @Inject
    public LoginService(SqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
    }

    @Start
    public void start(@Value(path = "sid") int sid) {
        userHexId = new HexId(sid);
    }

    public UserData userData(String userName) {
        SqlDataCache<UserData, String> cached = sqlDataHelper.getCacheService().cache(UserData.class);
        return cached.getIfPresent(userName);
    }

}
