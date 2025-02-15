package wxdgaming.boot2.starter.batis.sql.pgsql;

import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceModule;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;

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
        SqlConfig sqlConfig = BootConfig.getIns().getObject("db.mysql", SqlConfig.class);
        if (sqlConfig != null) {
            MysqlDataHelper dataHelper = new MysqlDataHelper(sqlConfig);
            bindSingleton(MysqlDataHelper.class, dataHelper);
        }
    }

}
