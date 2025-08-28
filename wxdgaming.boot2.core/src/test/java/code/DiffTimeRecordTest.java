package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.lang.DiffTimeRecord;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class DiffTimeRecordTest {

    @Test
    public void d1(){
        DiffTimeRecord record = DiffTimeRecord.start4Ms();
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        record.marker("start");
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
        record.marker("sleep");
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        record.marker("end");
        System.out.println(record.toString());
    }

}
