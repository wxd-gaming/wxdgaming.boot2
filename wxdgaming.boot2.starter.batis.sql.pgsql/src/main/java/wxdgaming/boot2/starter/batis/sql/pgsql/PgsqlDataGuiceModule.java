package wxdgaming.boot2.starter.batis.sql.pgsql;

import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;

/**
 * pgsql 模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:42
 **/
public class PgsqlDataGuiceModule extends ServiceGuiceModule {


    public PgsqlDataGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }

    @Override protected void bind() throws Throwable {
        {
            SqlConfig sqlConfig = BootConfig.getIns().getNestedValue("db.pgsql", SqlConfig.class);
            if (sqlConfig != null) {
                PgsqlDataHelper dataHelper = new PgsqlDataHelper(sqlConfig);
                bindInstance(PgsqlDataHelper.class, dataHelper);
                bindInstance(SqlDataHelper.class, dataHelper);
                bindInstance(PgsqlDataHelper.class, "db.pgsql", dataHelper);
                bindInstance(SqlDataHelper.class, "db.pgsql", dataHelper);
            }
        }
        {
            SqlConfig sqlConfig = BootConfig.getIns().getNestedValue("db.pgsql-second", SqlConfig.class);
            if (sqlConfig != null) {
                PgsqlDataHelper dataHelper = new PgsqlDataHelper(sqlConfig);
                bindInstance(PgsqlDataHelper.class, "db.pgsql-second", dataHelper);
                bindInstance(SqlDataHelper.class, "db.pgsql-second", dataHelper);
            }
        }
        {
            SqlConfig sqlConfig = BootConfig.getIns().getNestedValue("db.pgsql-third", SqlConfig.class);
            if (sqlConfig != null) {
                PgsqlDataHelper dataHelper = new PgsqlDataHelper(sqlConfig);
                bindInstance(PgsqlDataHelper.class, "db.pgsql-third", dataHelper);
                bindInstance(SqlDataHelper.class, "db.pgsql-third", dataHelper);
            }
        }
    }

}
