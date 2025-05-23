package wxdgaming.boot2.core.rank;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.Tuple2;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 排行榜容器, 如果要序列化存储到数据库中，请调用tolist方案，
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-20 20:57
 **/
@Getter
@Setter
public class RankMap {

    private ConcurrentHashMap<String, RankScore> map = new ConcurrentHashMap<>();
    private ConcurrentSkipListMap<RankScore, String> rankMap = new ConcurrentSkipListMap<>();

    public RankMap() {
    }

    public RankMap(List<RankScore> rankScores) {
        push(rankScores);
    }

    /** 所有的排行 */
    public void push(List<RankScore> rankScores) {
        rankScores.forEach(rankScore -> {
            map.put(rankScore.getKey(), rankScore);
            rankMap.remove(rankScore);
            rankMap.put(rankScore, rankScore.getKey());
        });
    }

    /**
     * 更新用户分数
     *
     * @param key      用户ID
     * @param newScore 新的分数
     */
    public RankScore updateScore(String key, long newScore) {
        // 原子性地更新用户分数
        return map.compute(key, (id, oldKey) -> {
            /*TODO 移除旧的ScoreKey*/
            if (oldKey != null) {
                if (oldKey.getScore() == newScore) {
                    /*TODO 无变化*/
                    return oldKey;
                }
                rankMap.remove(oldKey);
            } else {
                oldKey = new RankScore();
                oldKey.setKey(key);
            }
            oldKey.setScore(newScore);
            // 生成唯一时间戳（实际中可能需要分布式ID生成器）
            long timestamp = System.currentTimeMillis();
            oldKey.setTimestamp(timestamp);
            // 插入新的ScoreKey
            rankMap.put(oldKey, key);
            return oldKey;
        });
    }

    public int rank(String key) {
        RankScore rankScore = map.get(key);
        if (rankScore == null) {
            return -1;
        }
        /*返回它前面有多少数据*/
        return rankMap.headMap(rankScore).size() + 1;
    }

    public long score(String key) {
        RankScore rankScore = map.get(key);
        if (rankScore == null) {
            return -1;
        }
        return rankScore.getScore();
    }

    public RankScore rankData(String key) {
        return map.get(key);
    }

    public Tuple2<Integer, RankScore> rankTuple(String key) {
        RankScore rankScore = map.get(key);
        if (rankScore == null) {
            return new Tuple2<>(-1, null);
        }
        /*返回它前面有多少数据*/
        int r = rankMap.headMap(rankScore).size() + 1;
        return new Tuple2<>(r, rankScore);
    }

    public List<RankScore> topN(int n) {
        return rankMap.headMap(rankMap.lastKey(), true).keySet().stream().limit(n).toList();
    }

    public RankScore topByRank(int rank) {
        return rankMap.headMap(rankMap.lastKey(), true).keySet().stream().skip(rank - 1).limit(1).findFirst().orElse(null);
    }

    public List<RankScore> toList() {
        return topN(rankMap.size());
    }

}
