package wxdgaming.game.server.module.drive;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.shutdown;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.*;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 角色驱动
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 11:41
 **/
@Slf4j
@Singleton
public class PlayerDriveService extends HoldRunApplication {

    private final ConcurrentHashMap<Integer, PlayerDriveContent> playerDriveContentMap = new ConcurrentHashMap<>();
    private int logicCoreSize = 0;
    private final AtomicInteger onlineSize = new AtomicInteger();

    @Start
    public void start() {
        logicCoreSize = BootConfig.getIns().logicConfig().getCoreSize();
        for (int i = 0; i < logicCoreSize; i++) {
            PlayerDriveContent driveContent = new PlayerDriveContent("player-drive-" + i);
            playerDriveContentMap.put(i, driveContent);
            driveContent.timerJob = ExecutorFactory.getExecutorServiceLogic().scheduleAtFixedRate(driveContent, 33, 33, TimeUnit.MILLISECONDS);
        }
    }

    @Order(1)
    @shutdown()
    public void shutdown() {
        playerDriveContentMap.values().forEach(v -> v.timerJob.cancel(true));
    }

    public int onlineSize() {
        return onlineSize.get();
    }

    public int getPlayerDriveId(long uid) {
        return (int) (uid % logicCoreSize);
    }

    public String getPlayerDriveName(long uid) {
        return getPlayerDriveName(getPlayerDriveId(uid));
    }

    public String getPlayerDriveName(int driveId) {
        return "player-drive-" + driveId;
    }

    /** 提交到玩家队列处理任务 */
    public void executor(Player player, Runnable runnable) {
        String playerDriveName = getPlayerDriveName(player.getUid());

        ExecutorEvent executorEvent = new ExecutorEvent() {

            @Override public void onEvent() throws Exception {
                runnable.run();
            }

        };
        executorEvent.setQueueName(playerDriveName);
        ExecutorFactory.getExecutorServiceLogic().execute(executorEvent);
    }

    @OnLogin
    public void addPlayer(Player player) {
        int driveId = getPlayerDriveId(player.getUid());
        PlayerDriveContent playerDriveContent = playerDriveContentMap.get(driveId);
        playerDriveContent.playerMap.put(player.getUid(), player);
        onlineSize.incrementAndGet();
        if (log.isDebugEnabled()) {
            log.debug("PlayerHeartDrive add player {}", player);
        }
    }

    @OnLogout
    public void removePlayer(Player player) {
        int driveId = getPlayerDriveId(player.getUid());
        PlayerDriveContent playerDriveContent = playerDriveContentMap.get(driveId);
        Player remove = playerDriveContent.playerMap.remove(player.getUid());
        if (remove != null) {
            onlineSize.decrementAndGet();
        }
        if (log.isDebugEnabled()) {
            log.debug("PlayerHeartDrive add remove {}", player);
        }
    }

    public class PlayerDriveContent extends ExecutorEvent {

        private int lsatSecond = -1;
        private int lsatMinute = -1;
        private int lsatHour = -1;
        private int lastDay = -1;
        ScheduledFuture<?> timerJob;
        private final ConcurrentSkipListMap<Long, Player> playerMap = new ConcurrentSkipListMap<>();

        public PlayerDriveContent(String queueName) {
            this.queueName = queueName;
        }

        @Override public void onEvent() throws Exception {
            long millis = MyClock.millis();
            LocalDateTime localDateTime = MyClock.localDateTime(millis);
            int second = localDateTime.getSecond();
            int minute = localDateTime.getMinute();
            int hour = localDateTime.getHour();
            int day = localDateTime.getDayOfMonth();

            for (Player player : playerMap.values()) {
                runApplication.executeMethodWithAnnotatedException(OnHeart.class, player, millis);
                if (lsatSecond != second) {
                    lsatSecond = second;
                    runApplication.executeMethodWithAnnotatedException(OnHeartSecond.class, player, second);
                }
                if (lsatMinute != minute) {
                    lsatMinute = minute;
                    runApplication.executeMethodWithAnnotatedException(OnHeartMinute.class, player, minute);
                }
                if (lsatHour != hour && minute == 0) {
                    lsatHour = hour;
                    runApplication.executeMethodWithAnnotatedException(OnHeartHour.class, player, hour);
                }
                if (lastDay != day && hour == 0 && minute == 0) {
                    lastDay = day;
                    runApplication.executeMethodWithAnnotatedException(OnHeartDay.class, player, day);
                }
            }
        }
    }

}
