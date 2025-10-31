package wxdgaming.game.server.module.drive;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.core.event.StopBeforeEvent;
import wxdgaming.boot2.core.executor.*;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.common.slog.SlogService;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.slog.RoleInfoSlog;
import wxdgaming.game.server.event.EventConst;

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

    @EventListener
    public void start(StartEvent event) {

        for (int i = 0; i < logicCoreSize; i++) {
            PlayerDriveContent driveContent = new PlayerDriveContent("mapNpc-drive-" + i);
            playerDriveContentMap.put(i, driveContent);
            driveContent.timerJob = ExecutorFactory.getExecutorServiceLogic().scheduleAtFixedRate(driveContent, 33, 33, TimeUnit.MILLISECONDS);
        }
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
        return "mapNpc-drive-" + driveId;
    }

    /** 提交到玩家队列处理任务 */
    public void executor(Player player, Runnable runnable) {

        ExecutorEvent executorEvent = new ExecutorEvent() {

            @Override public void onEvent() throws Exception {
                runnable.run();
            }

        };
        executor(player, executorEvent);
    }

    public void executor(Player player, ExecutorEvent event) {
        String playerDriveName = getPlayerDriveName(player.getUid());
        event.setQueueName(playerDriveName);
        ExecutorFactory.getExecutorServiceLogic().execute(event);
    }

    @Order
    @EventListener
    public void addPlayer(EventConst.LoginPlayerEvent event) {
        Player player = event.player();
        int driveId = getPlayerDriveId(player.getUid());
        PlayerDriveContent playerDriveContent = playerDriveContentMap.get(driveId);
        playerDriveContent.playerMap.put(player.getUid(), player);
        onlineSize.incrementAndGet();
        if (log.isDebugEnabled()) {
            log.debug("PlayerHeartDrive add mapNpc {}", player);
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
                    applicationContextProvider.postEventIgnoreException(new EventConst.LogoutPlayerEvent(player));
                }
            }
        });

    }

    @Order
    @EventListener
    public void removePlayer(EventConst.LogoutPlayerEvent event) {
        Player player = event.player();
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

    @EventListener
    public void playerHeartMinuteEvent(EventConst.MapNpcHeartMinuteEvent event) {
        if (event.mapNpc() instanceof Player player) {
            updateRoleInfoSlog(player);
        }
    }

    public void updateRoleInfoSlog(Player player) {
        RoleInfoSlog roleInfoSlog = new RoleInfoSlog(player, String.valueOf(player.checkOnline()));
        slogService.updateLog(player.getUid(), MyClock.millis(), roleInfoSlog);
    }

    @Order(Integer.MIN_VALUE)
    @EventListener
    public void stopBefore(StopBeforeEvent event) {
        playerDriveContentMap.values().forEach(v -> v.timerJob.cancel(true));
        playerDriveContentMap.values().stream()
                .flatMap(v -> v.playerMap.values().stream())
                .forEach(player -> {
                    try {
                        player.getUserMapping().getSocketSession().close("停服");
                    } catch (Exception ignore) {}
                    updateRoleInfoSlog(player);
                });
    }

    public class PlayerDriveContent extends ExecutorEvent implements HeartDriveHandler {

        ScheduledFuture<?> timerJob;
        final HeartDrive heartDrive;
        final ConcurrentSkipListMap<Long, Player> playerMap = new ConcurrentSkipListMap<>();

        public PlayerDriveContent(String queueName) {
            this.queueName = queueName;
            this.heartDrive = new HeartDrive(this.queueName);
            this.heartDrive.setDriveHandler(this);
        }

        @Override public String getStack() {
            return this.queueName;
        }

        @Override public boolean isIgnoreRunTimeRecord() {
            return true;
        }

        @Override public void onEvent() throws Exception {
            heartDrive.doHeart();
        }

        @Override public void heart(long millis) {
            for (Player player : playerMap.values()) {
                applicationContextProvider.postEventIgnoreException(new EventConst.MapNpcHeartEvent(player));
            }
        }

        @Override public void heartSecond(int second) {
            for (Player player : playerMap.values()) {
                applicationContextProvider.postEventIgnoreException(new EventConst.MapNpcHeartSecondEvent(player, second));
            }
        }

        @Override public void heartMinute(int minute) {
            for (Player player : playerMap.values()) {
                applicationContextProvider.postEventIgnoreException(new EventConst.MapNpcHeartMinuteEvent(player, minute));
            }
        }

        @Override public void heartHour(int hour) {
            for (Player player : playerMap.values()) {
                applicationContextProvider.postEventIgnoreException(new EventConst.MapNpcHeartHourEvent(player, hour));
            }
        }

        @Override public void heartDayEnd(int dayOfYear) {
            for (Player player : playerMap.values()) {
                applicationContextProvider.postEventIgnoreException(new EventConst.MapNpcHeartDayEvent(player, dayOfYear));
            }
        }

        @Override public void heartWeek(long weekFirstDayStartTime) {
            for (Player player : playerMap.values()) {
                applicationContextProvider.postEventIgnoreException(new EventConst.MapNpcHeartWeekEvent(player, weekFirstDayStartTime));
            }
        }
    }

}
