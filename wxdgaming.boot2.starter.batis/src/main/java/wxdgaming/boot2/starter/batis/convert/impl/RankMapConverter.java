package wxdgaming.boot2.starter.batis.convert.impl;

import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.rank.RankMap;
import wxdgaming.boot2.core.rank.RankScore;
import wxdgaming.boot2.starter.batis.convert.Converter;

import java.lang.reflect.Type;
import java.util.List;

/**
 * RankMap 数据库转换器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-21 09:21
 **/
public class RankMapConverter extends Converter<RankMap, String> {

    @Override public String toDb(RankMap rankMap) {
        return FastJsonUtil.toJSONStringAsWriteType(rankMap.toList());
    }

    @Override public RankMap fromDb(Type type, String s) {
        List<RankScore> parseArray = FastJsonUtil.parseArray(s, RankScore.class);
        return new RankMap(parseArray);
    }

}
