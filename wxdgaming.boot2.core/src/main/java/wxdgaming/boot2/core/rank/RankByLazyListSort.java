package wxdgaming.boot2.core.rank;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.cache2.LRUIntCache;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 排行榜容器,延迟排序的懒惰容器
 * <br>
 * 当数据发生变化不会对排行榜立马进行排序，依赖缓存过期设置，适合带有缓存的排行榜数据
 * <br>
 * 如果需要可用调用{@link #forceRefresh()}方法进行强制刷新
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-20 20:57
 **/
@Getter
@Setter
public class RankByLazyListSort {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private final HashMap<String, RankScore> map = new HashMap<>();
    private final LRUIntCache<RankScore[]> rankCache;

    /**
     * 构造函数
     *
     * @param lazyTimeMs 排行榜延迟刷新时间
     */
    public RankByLazyListSort(long lazyTimeMs) {
        rankCache = LRUIntCache.<RankScore[]>builder()
                .expireAfterWriteMs(lazyTimeMs)
                .heartTimeMs(lazyTimeMs)
                .loader(k -> {
                    readLock.lock();
                    try {
                        Set<RankScore> rankScores = new TreeSet<>(map.values());
                        return rankScores.toArray(new RankScore[map.size()]);
                    } finally {
                        readLock.unlock();
                    }
                })
                .build();
        rankCache.start();
    }

    /**
     * 构造函数
     *
     * @param lazyTimeMs 排行榜延迟刷新时间
     * @param rankScores 排行榜容器数据
     */
    public RankByLazyListSort(long lazyTimeMs, List<RankScore> rankScores) {
        this(lazyTimeMs);
        push(rankScores);
    }

    /** 强制刷新 */
    public void forceRefresh() {
        rankCache.invalidate(0);
    }

    /** 所有的排行 */
    private void push(List<RankScore> rankScores) {
        writeLock.lock();
        try {
            rankScores.forEach(rankScore -> {
                RankScore old = map.put(rankScore.getKey(), rankScore);
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
    public RankScore updateScore(String key, long newScore) {
        return updateScore(key, newScore, System.nanoTime());
    }

    /**
     * 更新用户分数
     *
     * @param key       key
     * @param newScore  分数
     * @param timestamp 时间戳,建议使用 {@link System#nanoTime()}
     */
    public RankScore updateScore(String key, long newScore, long timestamp) {
        writeLock.lock();
        try {
            RankScore rankScore = map.computeIfAbsent(key, k -> {
                RankScore newRankScore = new RankScore().setKey(key);
                return newRankScore;
            });
            long oldScore = rankScore.getScore();
            if (oldScore != newScore) {
                rankScore.setScore(newScore);
                rankScore.setTimestamp(timestamp);
            }
            return rankScore;
        } finally {
            writeLock.unlock();
        }
    }

    public int rank(String key) {
        readLock.lock();
        try {
            RankScore rankScore = map.get(key);
            if (rankScore == null) {
                return -1;
            }
            RankScore[] rankScores = rankCache.get(0);
            RankScore score = null;
            for (int i = 0; i < rankScores.length; i++) {
                score = rankScores[i];
                if (score.getScore() > rankScore.getScore()) {
                    continue;
                }
                if (score.getKey().equals(key)) {
                    return i + 1;
                }
            }
            return -1;
        } finally {
            readLock.unlock();
        }
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

    /**
     * 根据排名获取用户数据
     *
     * @param rank 1 ~
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-05-26 11:01
     */
    public RankScore rankDataByRank(final int rank) {
        readLock.lock();
        try {
            AssertUtil.assertTrue(rank > 0, "从1开始");
            RankScore[] rankScores = rankCache.get(0);
            if (rankScores.length < rank) {
                return null;
            }
            return rankScores[rank - 1];
        } finally {
            readLock.unlock();
        }
    }

    public List<RankScore> rankByRange(int startRank, int endRank) {
        AssertUtil.assertTrue(startRank > 0, "从1开始");
        AssertUtil.assertTrue(endRank > 0 && endRank > startRank, "从1开始");
        readLock.lock();
        try {
            ArrayList<RankScore> resultRankScores = new ArrayList<>(endRank - startRank + 1);
            RankScore[] cache = rankCache.get(0);
            int firstRank = startRank;
            firstRank--;
            for (int i = firstRank; i < endRank; i++) {
                if (cache.length <= i) {
                    break;
                }
                RankScore rankScore = cache[i];
                resultRankScores.add(rankScore);
            }
            return resultRankScores;
        } finally {
            readLock.unlock();
        }
    }

    /** 返回前多少名 */
    public List<RankScore> rankBySize(int n) {
        readLock.lock();
        try {
            if (n <= 0) {
                return Collections.emptyList();
            }
            if (map.isEmpty()) {
                return List.of();
            }
            ArrayList<RankScore> rankScores = new ArrayList<>(n);
            RankScore[] cache = rankCache.get(0);
            for (int i = 0; i < cache.length; i++) {
                RankScore rankScore = cache[i];
                rankScores.add(rankScore);
                if (rankScores.size() >= n) {
                    break;
                }
            }
            return rankScores;
        } finally {
            readLock.unlock();
        }
    }

    public List<RankScore> toList() {
        return rankBySize(map.size());
    }

}
