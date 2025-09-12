package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.runtime.RunTimeUtil;

/**
 * 驱动代理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-12 16:50
 **/
@Getter
@Setter
class ThreadDriveHandlerProxy implements ThreadDriveHandler {

    private ThreadDriveHandler driveHandler;

    @Override public void heart() {
        long start = RunTimeUtil.start();
        try {
            driveHandler.heart();
        } finally {
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heart()", start);
        }
    }

    @Override public void heartSecond(int second) {
        long start = RunTimeUtil.start();
        try {
            driveHandler.heartSecond(second);
        } finally {
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartSecond()", start);
        }
    }

    @Override public void heartMinute(int minute) {
        long start = RunTimeUtil.start();
        try {

            driveHandler.heartMinute(minute);
        } finally {
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartMinute()", start);
        }
    }

    @Override public void heartHour(int hour) {
        long start = RunTimeUtil.start();
        try {
            driveHandler.heartHour(hour);
        } finally {
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartHour()", start);
        }
    }

    @Override public void heartDayEnd() {
        long start = RunTimeUtil.start();
        try {
            driveHandler.heartDayEnd();
        } finally {
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartDayEnd()", start);
        }
    }

    @Override public void heartWeek(long weekFirstDayStartTime) {
        long start = RunTimeUtil.start();
        try {
            driveHandler.heartWeek(weekFirstDayStartTime);
        } finally {
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartWeek()", start);
        }
    }
}
