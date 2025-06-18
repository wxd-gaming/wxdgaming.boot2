package wxdgaming.boot2.starter.batis.sql.pgsql;

import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.executor.ExecutorConfig;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;

/**
 * pgsql 模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:42
 **/
public class MysqlDataGuiceModule extends ServiceGuiceModule {


    public MysqlDataGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }

    @Override protected void bind() throws Throwable {
        {
            SqlConfig sqlConfig = BootConfig.getIns().getNestedValue("db.mysql", SqlConfig.class);
            if (sqlConfig != null) {
                MysqlDataHelper dataHelper = new MysqlDataHelper(sqlConfig);
                bindInstance(MysqlDataHelper.class, dataHelper);
                bindInstance(SqlDataHelper.class, dataHelper);
                bindInstance(MysqlDataHelper.class, "db.mysql", dataHelper);
                bindInstance(SqlDataHelper.class, "db.mysql", dataHelper);
            }
        }
        {
            SqlConfig sqlConfig = BootConfig.getIns().getNestedValue("db.mysql-second", SqlConfig.class);
            if (sqlConfig != null) {
                MysqlDataHelper dataHelper = new MysqlDataHelper(sqlConfig);
                bindInstance(MysqlDataHelper.class, "db.mysql-second", dataHelper);
                bindInstance(SqlDataHelper.class, "db.mysql-second", dataHelper);
            }
        }
        {
            SqlConfig sqlConfig = BootConfig.getIns().getNestedValue("db.mysql-third", SqlConfig.class);
            if (sqlConfig != null) {
                MysqlDataHelper dataHelper = new MysqlDataHelper(sqlConfig);
                bindInstance(MysqlDataHelper.class, "db.mysql-third", dataHelper);
                bindInstance(SqlDataHelper.class, "db.mysql-third", dataHelper);
            }
        }
    }

}
