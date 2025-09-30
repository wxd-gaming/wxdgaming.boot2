package wxdgaming.boot2.core.executor;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.SpringUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 心跳驱动线程
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-30 09:16
 **/
@Slf4j
public class HeartDriveThread extends Thread {

    private final HeartDrive heartDrive;

    public HeartDriveThread(String name) {
        super(name);
        this.heartDrive = new HeartDrive(name);
    }

    public void setDriveHandler(HeartDriveHandler driveHandler) {
        this.heartDrive.setDriveHandler(driveHandler);
    }

    @Override public void run() {
        while (!SpringUtil.exiting.get()) {
            try {
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(33));
                heartDrive.doHeart();
            } catch (Throwable throwable) {
                log.error("{}", this.getName(), throwable);
            }
        }
    }
}
