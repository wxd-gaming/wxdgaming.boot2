package wxdgaming.boot2.starter.batis.convert.impl;

import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.rank.RankByGroupMap;
import wxdgaming.boot2.core.rank.RankElement;
import wxdgaming.boot2.starter.batis.convert.AbstractConverter;

import java.lang.reflect.Type;
import java.util.List;

/**
 * RankMap 数据库转换器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-21 09:21
 **/
public class RankGroupMapConverter extends AbstractConverter<RankByGroupMap, String> {

    @Override public String toDb(RankByGroupMap source) {
        return FastJsonUtil.toJSONStringAsWriteType(source.toList());
    }

    @Override public RankByGroupMap fromDb(Type type, String s) {
        List<RankElement> parseArray = FastJsonUtil.parseArray(s, RankElement.class);
        return new RankByGroupMap(parseArray);
    }

}
