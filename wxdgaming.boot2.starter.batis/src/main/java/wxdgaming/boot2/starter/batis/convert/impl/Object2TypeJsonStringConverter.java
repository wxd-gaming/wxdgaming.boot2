package wxdgaming.boot2.starter.batis.convert.impl;

import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.starter.batis.convert.AbstractConverter;

import java.lang.reflect.Type;

/**
 * 任意对象转化成字节数组
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-16 10:02
 **/
public class Object2TypeJsonStringConverter extends AbstractConverter<Object, String> {

    @Override public String toDb(Object o) {
        return FastJsonUtil.toJSONStringAsWriteType(o);
    }

    @Override public Object fromDb(Type type, String json) {
        return FastJsonUtil.parse(json, type);
    }

}
