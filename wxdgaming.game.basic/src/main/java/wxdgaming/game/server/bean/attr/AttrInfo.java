package wxdgaming.game.server.bean.attr;


import com.alibaba.fastjson2.annotation.JSONType;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.HashMap;
import java.util.function.Function;

/**
 * 属性计算器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-03-23 20:39
 **/
@JSONType(seeAlso = {HashMap.class})
public class AttrInfo extends HashMap<AttrType, Long> {

    public static final Function<String, AttrInfo> JsonParse = json -> FastJsonUtil.parse(json, AttrInfo.class);

    public AttrInfo() {
    }

    public AttrInfo(AttrInfo attrInfo) {
        this.append(attrInfo);
    }

    @Override public Long get(Object key) {
        return super.getOrDefault(key, 0L);
    }

    public void append(AttrInfo attrInfo) {
        attrInfo.forEach((k, v) -> this.merge(k, v, Math::addExact));
    }

    public Long add(AttrType attrType, Long value) {
        return this.merge(attrType, value, Math::addExact);
    }

}
