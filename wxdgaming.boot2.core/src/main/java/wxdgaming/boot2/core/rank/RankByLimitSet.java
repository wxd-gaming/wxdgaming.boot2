package wxdgaming.boot2.core.rank;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 排行榜限制排行榜, 如果需要存入数据库使用 {@link #toDb()}
 * <p>排行榜数据记录的是前{@link #limit}名
 * <p>超过前{@link #limit}名的数据会只会记录分数段位的并列排名
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-27 16:02
 **/
@Getter
public class RankByLimitSet {

    private transient final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private transient final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private transient final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private final int limit;
    /** 记录前500名 */
    private final HashMap<String, RankScore> map = new HashMap<>();
    private final TreeSet<RankScore> rankTreeSet = new TreeSet<>();
    /** 根据分数阶段记录排名 */
    private final TreeMap<Long, Integer> scoreSizeMap = new TreeMap<>(Comparator.reverseOrder());

    public RankByLimitSet() {
        this(500);
    }

    public RankByLimitSet(int limit) {
        this.limit = limit;
    }

    public RankByLimitSet(int limit, Rank2Db rank2Db) {
        this.limit = limit;
        this.scoreSizeMap.putAll(rank2Db.getScoreSizeMap());
        push(rank2Db.rankScoreList);
    }

    /** 所有的排行 */
    private void push(List<RankScore> rankScores) {
        writeLock.lock();
        try {
            for (RankScore rankScore : rankScores) {
                map.put(rankScore.getKey(), rankScore);
                rankTreeSet.add(rankScore);
                scoreSizeMap.merge(rankScore.getScore(), 1, Math::addExact);
            }
            clearLimit();
        } finally {
            writeLock.unlock();
        }
    }

    private void clearLimit() {
        if (rankTreeSet.size() > limit) {
            for (int i = limit; i < rankTreeSet.size(); i++) {
                RankScore rankScore = rankTreeSet.last();
                map.remove(rankScore.getKey());
                rankTreeSet.remove(rankScore);
            }
        }
        scoreSizeMap.entrySet().removeIf(entry -> entry.getValue() == 0);
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
            RankScore rankScore = map.computeIfAbsent(key, k -> new RankScore().setKey(key));
            long oldScore = rankScore.getScore();
            if (oldScore != newScore) {

                if (oldScore != 0) {
                    scoreSizeMap.merge(oldScore, -1, Math::addExact);
                }

                rankTreeSet.remove(rankScore);

                rankScore.setScore(newScore);
                rankScore.setTimestamp(timestamp);

                rankTreeSet.add(rankScore);

                // 插入新的ScoreKey
                scoreSizeMap.merge(newScore, 1, Math::addExact);
                clearLimit();
            }
            return rankScore;
        } finally {
            writeLock.unlock();
        }
    }

    public int rank(String key, long score) {
        readLock.lock();
        try {
            RankScore rankScore = map.get(key);
            if (rankScore != null) {
                SortedSet<RankScore> rankScores = rankTreeSet.headSet(rankScore);
                return rankScores.size() + 1;
            }
            int rank = 0;
            for (Map.Entry<Long, Integer> longIntegerEntry : scoreSizeMap.entrySet()) {
                if (longIntegerEntry.getKey() > score) {
                    rank += longIntegerEntry.getValue();
                    continue;
                }
                /*TODO 相当于并列排名*/
                return rank;
            }
        } finally {
            readLock.unlock();
        }
        return -1;
    }

    public Collection<RankScore> limitValues() {
        return new ArrayList<>(rankTreeSet);
    }

    public Collection<RankScore> limitValues(int start, int end) {
        return new ArrayList<>(rankTreeSet).subList(start, end);
    }

    public Rank2Db toDb() {
        return new Rank2Db(this);
    }

    @Getter
    @Setter
    public static class Rank2Db {

        private List<RankScore> rankScoreList;
        /** 根据分数阶段记录排名 */
        private HashMap<Long, Integer> scoreSizeMap;

        public Rank2Db() {
        }

        public Rank2Db(RankByLimitSet rankByLimitSet) {
            this.rankScoreList = new ArrayList<>(rankByLimitSet.map.values());
            this.scoreSizeMap = new HashMap<>(rankByLimitSet.scoreSizeMap);
        }

    }

}
