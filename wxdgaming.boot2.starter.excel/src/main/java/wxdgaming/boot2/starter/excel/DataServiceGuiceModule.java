package wxdgaming.boot2.starter.excel;

import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.starter.excel.store.DataRepository;

/**
 * guice 注册模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 14:00
 **/
public class DataServiceGuiceModule extends ServiceGuiceModule {

    public DataServiceGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }

    @Override protected void bind() throws Throwable {
        bindInstance(DataRepository.getIns());
        bindInstance(ExcelRepository.getIns());
    }

}
