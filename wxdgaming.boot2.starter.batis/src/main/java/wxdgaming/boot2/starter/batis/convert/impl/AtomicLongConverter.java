package wxdgaming.boot2.starter.batis.convert.impl;

import wxdgaming.boot2.starter.batis.convert.Converter;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 任意对象转化成字节数组
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-16 10:02
 **/
public class AtomicLongConverter extends Converter<AtomicLong, Long> {

    @Override public Long toDb(AtomicLong o) {
        return o.longValue();
    }

    @Override public AtomicLong fromDb(Type type, Long hold) {
        return new AtomicLong(hold);
    }

}
