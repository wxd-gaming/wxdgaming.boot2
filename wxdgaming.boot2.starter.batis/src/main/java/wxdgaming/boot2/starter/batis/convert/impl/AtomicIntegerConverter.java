package wxdgaming.boot2.starter.batis.convert.impl;

import wxdgaming.boot2.starter.batis.convert.Converter;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任意对象转化成字节数组
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-16 10:02
 **/
public class AtomicIntegerConverter extends Converter<AtomicInteger, Integer> {

    @Override public Integer toDb(AtomicInteger o) {
        return o.intValue();
    }

    @Override public AtomicInteger fromDb(Type type, Integer hold) {
        return new AtomicInteger(hold);
    }

}
