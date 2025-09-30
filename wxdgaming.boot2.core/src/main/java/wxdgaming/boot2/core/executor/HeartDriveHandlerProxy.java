package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.runtime.RunTimeUtil;

/**
 * 驱动代理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-12 16:50
 **/
@Slf4j
@Getter
@Setter
class HeartDriveHandlerProxy implements HeartDriveHandler {

    private HeartDriveHandler driveHandler;

    @Override public void heart(long millis) {
        long start = RunTimeUtil.start();
        try {
            driveHandler.heart(millis);
        } catch (Exception e) {
            log.error("{}.heart() error", driveHandler.getClass().getName(), e);
        } finally {
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heart()", start);
        }
    }

    @Override public void heartSecond(int second) {
        long start = RunTimeUtil.start();
        try {
            driveHandler.heartSecond(second);
        } catch (Exception e) {
            log.error("{}.heartSecond() error", driveHandler.getClass().getName(), e);
        } finally {
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartSecond()", start);
        }
    }

    @Override public void heartMinute(int minute) {
        long start = RunTimeUtil.start();
        try {

            driveHandler.heartMinute(minute);
        } catch (Exception e) {
            log.error("{}.heartMinute() error", driveHandler.getClass().getName(), e);
        } finally {
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartMinute()", start);
        }
    }

    @Override public void heartHour(int hour) {
        long start = RunTimeUtil.start();
        try {
            driveHandler.heartHour(hour);
        } catch (Exception e) {
            log.error("{}.heartHour() error", driveHandler.getClass().getName(), e);
        } finally {
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartHour()", start);
        }
    }

    @Override public void heartDayEnd(int dayOfYear) {
        long start = RunTimeUtil.start();
        try {
            driveHandler.heartDayEnd(dayOfYear);
        } catch (Exception e) {
            log.error("{}.heartDayEnd() error", driveHandler.getClass().getName(), e);
        } finally {
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartDayEnd()", start);
        }
    }

    @Override public void heartWeek(long weekFirstDayStartTime) {
        long start = RunTimeUtil.start();
        try {
            driveHandler.heartWeek(weekFirstDayStartTime);
        } catch (Exception e) {
            log.error("{}.heartWeek() error", driveHandler.getClass().getName(), e);
        } finally {
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartWeek()", start);
        }
    }
}
