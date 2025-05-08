package wxdgaming.game.test.module.drive;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.threading.Event;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.threading.TimerJob;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.event.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

/**
 * 角色驱动
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 11:41
 **/
@Slf4j
@Singleton
public class PlayerDrive extends HoldRunApplication {

    private final HashMap<Integer, PlayerDriveContent> playerDriveContentMap = new HashMap<>();

    @Start
    public void start(ExecutorUtil executorUtil) {
        int maxSize = BootConfig.getIns().logicConfig().getMaxSize();
        for (int i = 0; i < maxSize; i++) {
            PlayerDriveContent driveContent = new PlayerDriveContent();
            playerDriveContentMap.put(i, driveContent);
            driveContent.timerJob = executorUtil.getLogicExecutor().scheduleAtFixedDelay("drive-" + i, driveContent, 33, 33, TimeUnit.MILLISECONDS);
        }
    }

    @OnLogin
    public void addPlayer(Player player) {
        int driveId = (int) (player.getUid() % playerDriveContentMap.size());
        PlayerDriveContent playerDriveContent = playerDriveContentMap.computeIfAbsent(driveId, k -> new PlayerDriveContent());
        playerDriveContent.playerMap.put(player.getUid(), player);
    }

    @OnLogout
    public void removePlayer(Player player) {
        int driveId = (int) (player.getUid() % playerDriveContentMap.size());
        PlayerDriveContent playerDriveContent = playerDriveContentMap.computeIfAbsent(driveId, k -> new PlayerDriveContent());
        playerDriveContent.playerMap.remove(player.getUid());
    }

    public class PlayerDriveContent extends Event {

        private int lsatSecond = -1;
        private int lsatMinute = -1;
        private int lsatHour = -1;
        private int lastDay = -1;
        TimerJob timerJob;
        private final ConcurrentSkipListMap<Long, Player> playerMap = new ConcurrentSkipListMap<>();

        public PlayerDriveContent() {
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
