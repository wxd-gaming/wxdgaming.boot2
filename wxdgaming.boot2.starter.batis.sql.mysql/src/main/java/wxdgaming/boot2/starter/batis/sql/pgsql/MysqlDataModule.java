package wxdgaming.boot2.starter.batis.sql.pgsql;

import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceModule;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;

/**
 * pgsql 模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:42
 **/
public class MysqlDataModule extends ServiceModule {


    public MysqlDataModule(ReflectContext reflectContext) {
        super(reflectContext);
    }

    @Override protected void bind() throws Throwable {
        {
            SqlConfig sqlConfig = BootConfig.getIns().getNestedValue("db.mysql", SqlConfig.class);
            if (sqlConfig != null) {
                MysqlService dataHelper = new MysqlService(sqlConfig);
                bindInstance(MysqlService.class, dataHelper);
                bindInstance(SqlDataHelper.class, dataHelper);
            }
        }
        {
            SqlConfig sqlConfig = BootConfig.getIns().getNestedValue("db.mysql-second", SqlConfig.class);
            if (sqlConfig != null) {
                MysqlService2 dataHelper = new MysqlService2(sqlConfig);
                bindInstance(dataHelper);
            }
        }
        {
            SqlConfig sqlConfig = BootConfig.getIns().getNestedValue("db.mysql-third", SqlConfig.class);
            if (sqlConfig != null) {
                MysqlService3 dataHelper = new MysqlService3(sqlConfig);
                bindInstance(dataHelper);
            }
        }
    }

}
