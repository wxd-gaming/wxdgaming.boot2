package wxdgaming.boot2.starter.batis.build;

import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.batis.TableMapping;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 构建工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-26 21:04
 **/
public class BuildColumnFactory implements IColumnFactory {

    private static final class Lazy {

        private static final AtomicBoolean INIT = new AtomicBoolean(false);
        private static final BuildColumnFactory INSTANCE = new BuildColumnFactory();

        static void init() {
            if (INIT.compareAndSet(false, true)) {
                ReflectProvider reflectProvider = ReflectProvider.Builder.of(Lazy.class.getClassLoader(), Lazy.class.getPackageName()).build();
                reflectProvider.classWithSuper(IBuildColumn.class).forEach(cls -> {
                    IBuildColumn buildColumn = ReflectProvider.newInstance(cls);
                    buildColumn.register(INSTANCE);
                });
            }
        }
    }

    public static BuildColumnFactory getInstance() {
        Lazy.init();
        return Lazy.INSTANCE;
    }

    private final HashMap<Class<?>, IBuildColumn> buildColumnMap = new HashMap<>();

    BuildColumnFactory() {

    }

    @Override public void register(Class<?> type, IBuildColumn buildColumn) {
        IBuildColumn put = buildColumnMap.put(type, buildColumn);
        AssertUtil.isTrue(put == null, "类型重复注册 %s %s %s", type, put, buildColumn);
    }

    public void buildColumn(TableMapping.FieldMapping fieldMapping) {
        Class<?> type = fieldMapping.getFieldProvider().getField().getType();
        if (AtomicReference.class.isAssignableFrom(type)) {
            type = ReflectProvider.getTType(fieldMapping.getFieldProvider().getField().getGenericType(), 0);
        }
        IBuildColumn orDefault = buildColumnMap.getOrDefault(type, buildColumnMap.get(String.class));
        orDefault.buildColumn(fieldMapping);
    }

}
