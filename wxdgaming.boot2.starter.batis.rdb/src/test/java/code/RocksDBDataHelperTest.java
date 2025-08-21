package code;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.rocksdb.RocksIterator;
import wxdgaming.boot2.starter.batis.rdb.RocksDBConfig;
import wxdgaming.boot2.starter.batis.rdb.RocksDBDataHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class RocksDBDataHelperTest {

    static RocksDBDataHelper rocksDBDataHelper;

    static {
        rocksDBDataHelper = new RocksDBDataHelper(new RocksDBConfig());
    }

    public static void main(String[] args) {

        RocksIterator iterator = rocksDBDataHelper.iterator();

        // 正序遍历
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            System.out.println("Key: " + new String(iterator.key()) + ", Value: " + new String(iterator.value()));
        }

        rocksDBDataHelper.put(String.valueOf(System.currentTimeMillis()), "123");

    }

    @Test
    public void t1() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executorService.execute(() -> {
                rocksDBDataHelper.put(String.valueOf(System.currentTimeMillis()) + "-" + finalI, "123-" + finalI);
            });
        }
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
        // 正序遍历
        RocksIterator iterator = rocksDBDataHelper.iterator();
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            System.out.println("Key: " + new String(iterator.key()) + ", Value: " + new String(iterator.value()));
        }
    }

}
