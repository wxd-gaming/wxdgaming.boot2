package wxdgaming.boot2.starter.batis.mapdb;

import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.reflect.ReflectProvider;

/**
 * guice 注册模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 14:00
 **/
public class MapDBGuiceModule extends ServiceGuiceModule {

    public MapDBGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }

    @Override protected void bind() throws Throwable {
        String dbMapdbPath = BootConfig.getIns().getNestedValue("db.mapdb.path", String.class);
        if (StringUtils.isNotBlank(dbMapdbPath)) {
            MapDBDataHelper mapDBDataHelper = new MapDBDataHelper(dbMapdbPath);
            bindInstance(MapDBDataHelper.class, mapDBDataHelper);
        }
    }

}
