package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.starter.batis.rocksdb.RocksDBHelper;

public class RocksDBHelperTest {

    @Test
    public void test() {
        RocksDBHelper rocksDBHelper = new RocksDBHelper("target/test.db");
        System.out.println(rocksDBHelper.getString("test"));
        rocksDBHelper.put("test", "test");
        System.out.println(rocksDBHelper.getString("test"));
    }

}
