package wxdgaming.boot2.core.rank;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.locks.MonitorReadWrite;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.*;

/**
 * 集中型排行榜容器, 如果要序列化存储到数据库中，请调用tolist方案
 * <br>
 * 利用set的自动排序功能，每次更新分数时，会自动排序
 * <br>
 * 集中型排行榜容器，比较适合比如副本通关排行，3星通过，2星通过，由于大部分的数据会是3星，或者2星这种重叠概念，所以更加高效
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-20 20:57
 **/
@Getter
@Setter
public class RankByGroupMap extends MonitorReadWrite {

    private final HashMap<String, RankElement> map = new HashMap<>();
    @JSONField(serialize = false, deserialize = false)
    private final transient TreeMap<Long, TreeSet<RankElement>> rankScoreMap = new TreeMap<>(Comparator.reverseOrder());

    public RankByGroupMap() {

    }

    public RankByGroupMap(List<RankElement> rankElements) {
        push(rankElements);
    }

    /** 所有的排行 */
    private void push(List<RankElement> rankElements) {
        writeLock.lock();
        try {
            rankElements.forEach(rankScore -> {
                RankElement oldRankElement = map.get(rankScore.getKey());
                long oldScore = oldRankElement.getScore();
                if (oldScore != rankScore.getScore()) {
                    TreeSet<RankElement> oldRankElementSet = rankScoreMap.get(oldScore);
                    if (oldRankElementSet != null) {
                        oldRankElementSet.remove(oldRankElement);
                        if (oldRankElementSet.isEmpty()) {
                            rankScoreMap.remove(oldScore);
                        }
                    }
                    // 插入新的ScoreKey
                    rankScoreMap.computeIfAbsent(rankScore.getScore(), l -> new TreeSet<>()).add(rankScore);
                }
            });
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 更新用户分数
     *
     * @param key      用户ID
     * @param newScore 新的分数
     */
    public RankElement updateScore(String key, long newScore) {
        return updateScore(key, newScore, System.nanoTime());
    }

    /**
     * 更新用户分数
     *
     * @param key       key
     * @param newScore  分数
     * @param timestamp 时间戳,建议使用 {@link System#nanoTime()}
     */
    public RankElement updateScore(String key, long newScore, long timestamp) {
        writeLock.lock();
        try {
            RankElement rankElement = map.computeIfAbsent(key, k -> new RankElement().setKey(key));
            long oldScore = rankElement.getScore();
            if (oldScore != newScore) {
                TreeSet<RankElement> oldRankElementSet = rankScoreMap.get(oldScore);
                if (oldRankElementSet != null) {
                    oldRankElementSet.remove(rankElement);
                    if (oldRankElementSet.isEmpty()) {
                        rankScoreMap.remove(oldScore);
                    }
                }

                rankElement.setScore(newScore);
                rankElement.setTimestamp(timestamp);
                // 插入新的ScoreKey
                rankScoreMap.computeIfAbsent(newScore, l -> new TreeSet<>()).add(rankElement);
            }
            return rankElement;
        } finally {
            writeLock.unlock();
        }
    }

    public int rank(String key) {
        readLock.lock();
        try {
            RankElement rankElement = map.get(key);
            if (rankElement == null) {
                return -1;
            }
            int rank = 0;
            for (Map.Entry<Long, TreeSet<RankElement>> entry : rankScoreMap.entrySet()) {
                if (entry.getKey() > rankElement.getScore()) {
                    rank += entry.getValue().size();
                } else {
                    for (RankElement value : entry.getValue()) {
                        rank++;
                        if (value.getKey().equals(key)) {
                            return rank;
                        }
                    }
                    return -1;
                }
            }
            return -1;
        } finally {
            readLock.unlock();
        }
    }

    public long score(String key) {
        RankElement rankElement = map.get(key);
        if (rankElement == null) {
            return -1;
        }
        return rankElement.getScore();
    }

    public RankElement rankData(String key) {
        return map.get(key);
    }

    public RankElement rankDataByRank(final int rank) {
        readLock.lock();
        try {
            AssertUtil.isTrue(rank > 0, "rank must be greater than 0");
            if (map.size() < rank) {
                return null;
            }
            int currentRank = 0;
            for (Map.Entry<Long, TreeSet<RankElement>> entry : rankScoreMap.entrySet()) {
                if (entry.getValue().size() + currentRank < rank) {
                    currentRank += entry.getValue().size();
                } else {
                    for (RankElement value : entry.getValue()) {
                        currentRank++;
                        if (currentRank >= rank) {
                            return value;
                        }
                    }
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }

    public List<RankElement> rankByRange(int startRank, int endRank) {
        return rankByRange(startRank, true, endRank, true);
    }

    public List<RankElement> rankByRange(int startRank, boolean hasStart, int endRank, boolean hasEnd) {
        readLock.lock();
        try {
            ArrayList<RankElement> rankElements = new ArrayList<>(endRank - startRank + 1);
            int currentRank = 0;
            for (Map.Entry<Long, TreeSet<RankElement>> entry : rankScoreMap.entrySet()) {
                TreeSet<RankElement> rankElementSet = entry.getValue();
                for (RankElement rankElement : rankElementSet) {
                    currentRank++;
                    if (currentRank < startRank) {
                        continue;
                    }
                    if (currentRank == startRank && !hasStart) {
                        continue;
                    }
                    rankElements.add(rankElement);
                    if (currentRank > endRank || (currentRank == endRank && !hasEnd)) {
                        return rankElements;
                    }
                }
            }
            return rankElements;
        } finally {
            readLock.unlock();
        }
    }

    /** 返回前多少名 */
    public List<RankElement> rankBySize(int n) {
        readLock.lock();
        try {
            if (n <= 0) {
                return Collections.emptyList();
            }
            if (map.isEmpty()) {
                return Collections.emptyList();
            }
            ArrayList<RankElement> rankElements = new ArrayList<>(n);
            for (Map.Entry<Long, TreeSet<RankElement>> entry : rankScoreMap.entrySet()) {
                TreeSet<RankElement> rankElementSet = entry.getValue();
                for (RankElement rankElement : rankElementSet) {
                    rankElements.add(rankElement);
                    if (rankElements.size() >= n) {
                        return rankElements;
                    }
                }
            }
            return rankElements;
        } finally {
            readLock.unlock();
        }
    }

    public List<RankElement> toList() {
        return rankBySize(rankScoreMap.size());
    }

}
