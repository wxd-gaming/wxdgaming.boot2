package wxdgaming.boot2.starter.batis.convert.impl;

import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.starter.batis.convert.Converter;

import java.lang.reflect.Type;

/**
 * 任意对象转化成字节数组
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-16 10:02
 **/
public class Object2TypeJsonStringConverter extends Converter<Object, String> {

    @Override public String toDb(Object o) {
        return FastJsonUtil.toJSONString(o, FastJsonUtil.Writer_Type_Name_Features_K_V_String);
    }

    @Override public Object fromDb(Type type, String json) {
        return FastJsonUtil.parse(json, type);
    }

}
