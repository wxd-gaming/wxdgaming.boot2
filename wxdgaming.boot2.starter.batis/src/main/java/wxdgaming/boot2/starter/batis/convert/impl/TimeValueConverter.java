package wxdgaming.boot2.starter.batis.convert.impl;

import wxdgaming.boot2.core.lang.TimeValue;
import wxdgaming.boot2.starter.batis.convert.Converter;

import java.lang.reflect.Type;

/**
 * 任意对象转化成字节数组
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-16 10:02
 **/
public class TimeValueConverter extends Converter<TimeValue, Long> {

    @Override public Long toDb(TimeValue o) {
        return o.longValue();
    }

    @Override public TimeValue fromDb(Type type, Long hold) {
        return new TimeValue(hold);
    }

}
