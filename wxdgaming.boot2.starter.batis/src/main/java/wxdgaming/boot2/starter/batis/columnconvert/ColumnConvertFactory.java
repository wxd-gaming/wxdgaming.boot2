package wxdgaming.boot2.starter.batis.columnconvert;

import wxdgaming.boot2.core.reflect.ReflectProvider;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 字段转换工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-27 11:43
 **/
public class ColumnConvertFactory extends AbstractColumnConvertFactory {

    private static final class Lazy {
        private static final ColumnConvertFactory INSTANCE = new ColumnConvertFactory();
        private static final AtomicBoolean INIT = new AtomicBoolean(false);

        static void init() {
            if (INIT.compareAndSet(false, true)) {
                ReflectProvider reflectProvider = ReflectProvider.Builder.of(
                                ColumnConvertFactory.Lazy.class.getClassLoader(),
                                ColumnConvertFactory.Lazy.class.getPackageName())
                        .build();
                reflectProvider.classWithSuper(IColumnConvert.class).forEach(cls -> {
                    IColumnConvert columnConvert = ReflectProvider.newInstance(cls);
                    columnConvert.register(INSTANCE);
                });
            }
        }
    }

    public static ColumnConvertFactory getInstance() {
        Lazy.init();
        return Lazy.INSTANCE;
    }

    ColumnConvertFactory() {}

}
