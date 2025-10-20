package wxdgaming.boot2.core.rank;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.cache2.LRUIntCache;
import wxdgaming.boot2.core.locks.MonitorReadWrite;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.*;

/**
 * 排行榜容器,延迟排序的懒惰容器
 * <br>
 * 当数据发生变化不会对排行榜立马进行排序，依赖缓存过期设置，适合带有缓存的排行榜数据
 * <br>
 * 如果需要可用调用{@link #forceRefresh()}方法进行强制刷新
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-20 20:57
 **/
@Getter
@Setter
public class RankByLazyListSort extends MonitorReadWrite {

    private final HashMap<String, RankElement> map = new HashMap<>();
    @JSONField(serialize = false, deserialize = false)
    private final transient LRUIntCache<RankElement[]> rankCache;

    /**
     * 构造函数
     *
     * @param lazyTimeMs 排行榜延迟刷新时间
     */
    public RankByLazyListSort(long lazyTimeMs) {
        rankCache = LRUIntCache.<RankElement[]>builder()
                .expireAfterWriteMs(lazyTimeMs)
                .heartTimeMs(lazyTimeMs)
                .loader(k -> {
                    readLock.lock();
                    try {
                        Set<RankElement> rankElements = new TreeSet<>(map.values());
                        return rankElements.toArray(new RankElement[map.size()]);
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
     * @param lazyTimeMs   排行榜延迟刷新时间
     * @param rankElements 排行榜容器数据
     */
    public RankByLazyListSort(long lazyTimeMs, List<RankElement> rankElements) {
        this(lazyTimeMs);
        push(rankElements);
    }

    /** 强制刷新 */
    public void forceRefresh() {
        rankCache.invalidate(0);
    }

    /** 所有的排行 */
    private void push(List<RankElement> rankElements) {
        writeLock.lock();
        try {
            rankElements.forEach(rankScore -> {
                RankElement old = map.put(rankScore.getKey(), rankScore);
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
            RankElement rankElement = map.computeIfAbsent(key, k -> {
                RankElement newRankElement = new RankElement().setKey(key);
                return newRankElement;
            });
            long oldScore = rankElement.getScore();
            if (oldScore != newScore) {
                rankElement.setScore(newScore);
                rankElement.setTimestamp(timestamp);
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
            RankElement[] rankElements = rankCache.get(0);
            RankElement score = null;
            for (int i = 0; i < rankElements.length; i++) {
                score = rankElements[i];
                if (score.getScore() > rankElement.getScore()) {
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
        RankElement rankElement = map.get(key);
        if (rankElement == null) {
            return -1;
        }
        return rankElement.getScore();
    }

    public RankElement rankData(String key) {
        return map.get(key);
    }

    /**
     * 根据排名获取用户数据
     *
     * @param rank 1 ~
     * @return
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2025-05-26 11:01
     */
    public RankElement rankDataByRank(final int rank) {
        readLock.lock();
        try {
            AssertUtil.isTrue(rank > 0, "从1开始");
            RankElement[] rankElements = rankCache.get(0);
            if (rankElements.length < rank) {
                return null;
            }
            return rankElements[rank - 1];
        } finally {
            readLock.unlock();
        }
    }

    public List<RankElement> rankByRange(int startRank, int endRank) {
        AssertUtil.isTrue(startRank > 0, "从1开始");
        AssertUtil.isTrue(endRank > 0 && endRank > startRank, "从1开始");
        readLock.lock();
        try {
            ArrayList<RankElement> resultRankElements = new ArrayList<>(endRank - startRank + 1);
            RankElement[] cache = rankCache.get(0);
            int firstRank = startRank;
            firstRank--;
            for (int i = firstRank; i < endRank; i++) {
                if (cache.length <= i) {
                    break;
                }
                RankElement rankElement = cache[i];
                resultRankElements.add(rankElement);
            }
            return resultRankElements;
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
            RankElement[] cache = rankCache.get(0);
            for (int i = 0; i < cache.length; i++) {
                RankElement rankElement = cache[i];
                rankElements.add(rankElement);
                if (rankElements.size() >= n) {
                    break;
                }
            }
            return rankElements;
        } finally {
            readLock.unlock();
        }
    }

    public List<RankElement> toList() {
        return rankBySize(map.size());
    }

}
