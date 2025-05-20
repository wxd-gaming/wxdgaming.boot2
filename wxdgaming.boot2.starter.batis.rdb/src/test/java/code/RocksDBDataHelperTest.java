package code;

import lombok.extern.slf4j.Slf4j;
import org.rocksdb.RocksIterator;
import wxdgaming.boot2.starter.batis.rdb.RocksDBDataHelper;

@Slf4j
public class RocksDBDataHelperTest {

    public static void main(String[] args) {

        RocksDBDataHelper rocksDBDataHelper = new RocksDBDataHelper();
        RocksIterator iterator = rocksDBDataHelper.iterator();

        // 正序遍历
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            System.out.println("Key: " + new String(iterator.key()) + ", Value: " + new String(iterator.value()));
        }

        rocksDBDataHelper.put(String.valueOf(System.currentTimeMillis()), "123");

    }

}
