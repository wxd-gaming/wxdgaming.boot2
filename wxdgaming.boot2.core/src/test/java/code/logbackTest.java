package code;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class logbackTest {

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            String stringBuilder = "a".repeat(100000) + System.nanoTime();
            log.info(stringBuilder);
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        }
        System.out.println(333);
    }

}
