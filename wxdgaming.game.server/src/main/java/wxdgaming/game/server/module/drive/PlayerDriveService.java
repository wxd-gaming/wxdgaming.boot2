package wxdgaming.game.server.module.drive;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.Stop;
import wxdgaming.boot2.core.ann.StopBefore;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorProperties;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.common.slog.SlogService;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.slog.RoleInfoSlog;
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
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 11:41
 **/
@Slf4j
@Service
public class PlayerDriveService extends HoldApplicationContext {

    private final ConcurrentHashMap<Integer, PlayerDriveContent> playerDriveContentMap = new ConcurrentHashMap<>();
    private final int logicCoreSize;
    private final AtomicInteger onlineSize = new AtomicInteger();
    final SlogService slogService;

    public PlayerDriveService(ExecutorProperties executorProperties, SlogService slogService) {
        logicCoreSize = executorProperties.getLogic().getCoreSize();
        this.slogService = slogService;
    }

    @Start
    public void start() {

        for (int i = 0; i < logicCoreSize; i++) {
            PlayerDriveContent driveContent = new PlayerDriveContent("player-drive-" + i);
            playerDriveContentMap.put(i, driveContent);
            driveContent.timerJob = ExecutorFactory.getExecutorServiceLogic().scheduleAtFixedRate(driveContent, 33, 33, TimeUnit.MILLISECONDS);
        }
    }

    @Order(1)
    @StopBefore
    public void stopBefore() {
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
        updateRoleInfoSlog(player);

        UserMapping userMapping = player.getUserMapping();
        Channel selfChannel = userMapping.getSocketSession().getChannel();
        selfChannel.closeFuture().addListener(future -> {
            SocketSession socketSession = userMapping.getSocketSession();
            if (socketSession != null) {
                if (selfChannel == socketSession.getChannel()) {
                    log.info(
                            "sid={}, account={} 角色下线 rid={} -> {}",
                            userMapping.getSid(), userMapping.getAccount(), userMapping.getRid(), player.getUid()
                    );
                    applicationContextProvider.executeMethodWithAnnotatedException(OnLogout.class, player);
                }
            }
        });

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
        updateRoleInfoSlog(player);
    }

    @OnHeartMinute
    public void updateRoleInfoSlog(Player player) {
        RoleInfoSlog roleInfoSlog = new RoleInfoSlog(player, String.valueOf(player.checkOnline()), 1);
        slogService.updateLog(player.getUid(), player.getCreateTime(), roleInfoSlog);
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
                applicationContextProvider.executeMethodWithAnnotatedException(OnHeart.class, player, millis);
                if (lsatSecond != second) {
                    lsatSecond = second;
                    applicationContextProvider.executeMethodWithAnnotatedException(OnHeartSecond.class, player, second);
                }
                if (lsatMinute != minute) {
                    lsatMinute = minute;
                    applicationContextProvider.executeMethodWithAnnotatedException(OnHeartMinute.class, player, minute);
                }
                if (lsatHour != hour && minute == 0) {
                    lsatHour = hour;
                    applicationContextProvider.executeMethodWithAnnotatedException(OnHeartHour.class, player, hour);
                }
                if (lastDay != day && hour == 0 && minute == 0) {
                    lastDay = day;
                    applicationContextProvider.executeMethodWithAnnotatedException(OnHeartDay.class, player, day);
                }
            }
        }
    }

}
