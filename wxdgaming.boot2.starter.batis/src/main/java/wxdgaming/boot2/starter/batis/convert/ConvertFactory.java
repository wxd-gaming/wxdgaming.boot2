package wxdgaming.boot2.starter.batis.convert;

import wxdgaming.boot2.core.reflect.ReflectProvider;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 转换工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-16 10:56
 **/
public class ConvertFactory {

    public static final ConcurrentHashMap<Class<? extends AbstractConverter>, AbstractConverter<?, ?>> converterMap = new ConcurrentHashMap<>();

    @SuppressWarnings("rawtypes")
    public static AbstractConverter getConverter(Class<? extends AbstractConverter> cls) {
        return converterMap.computeIfAbsent(cls, l -> ReflectProvider.newInstance(cls));
    }

}
