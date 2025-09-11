package wxdgaming.logserver.module.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.logserver.LogServerProperties;

/**
 * 服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-10 16:29
 **/
@Slf4j
@Service
public class AdminService extends HoldApplicationContext {

    private final LogServerProperties logServerProperties;
    private final SqlDataHelper sqlDataHelper;

    public AdminService(LogServerProperties logServerProperties, PgsqlDataHelper pgsqlDataHelper) {
        this.logServerProperties = logServerProperties;
        this.sqlDataHelper = pgsqlDataHelper;
    }

}
