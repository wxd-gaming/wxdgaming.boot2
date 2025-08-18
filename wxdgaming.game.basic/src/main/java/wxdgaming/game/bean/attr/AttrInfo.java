package wxdgaming.game.bean.attr;


import com.alibaba.fastjson.annotation.JSONType;

import java.util.HashMap;

/**
 * 属性计算器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-03-23 20:39
 **/
@JSONType(seeAlso = {HashMap.class})
public class AttrInfo extends HashMap<AttrType, Long> {

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
