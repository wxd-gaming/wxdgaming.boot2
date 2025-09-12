package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.DiffTimeRecord;
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
        DiffTimeRecord diffTimeRecord = DiffTimeRecord.start4Ns();
        try {
            driveHandler.heart();
        } finally {
            long interval = diffTimeRecord.interval().interval();
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heart()", interval);
        }
    }

    @Override public void heartSecond(int second) {
        DiffTimeRecord diffTimeRecord = DiffTimeRecord.start4Ns();
        try {
            driveHandler.heartSecond(second);
        } finally {
            long interval = diffTimeRecord.interval().interval();
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartSecond()", interval);
        }
    }

    @Override public void heartMinute(int minute) {
        DiffTimeRecord diffTimeRecord = DiffTimeRecord.start4Ns();
        try {

            driveHandler.heartMinute(minute);
        } finally {
            long interval = diffTimeRecord.interval().interval();
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartMinute()", interval);
        }
    }

    @Override public void heartHour(int hour) {

        DiffTimeRecord diffTimeRecord = DiffTimeRecord.start4Ns();
        try {
            driveHandler.heartHour(hour);
        } finally {
            long interval = diffTimeRecord.interval().interval();
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartHour()", interval);
        }
    }

    @Override public void heartDayEnd() {
        DiffTimeRecord diffTimeRecord = DiffTimeRecord.start4Ns();
        try {
            driveHandler.heartDayEnd();
        } finally {
            long interval = diffTimeRecord.interval().interval();
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartDayEnd()", interval);
        }
    }

    @Override public void heartWeek(long weekFirstDayStartTime) {
        DiffTimeRecord diffTimeRecord = DiffTimeRecord.start4Ns();
        try {
            driveHandler.heartWeek(weekFirstDayStartTime);
        } finally {
            long interval = diffTimeRecord.interval().interval();
            RunTimeUtil.record(driveHandler.getClass().getSimpleName() + "#heartWeek()", interval);
        }
    }
}
