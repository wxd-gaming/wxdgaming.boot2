package code;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.starter.batis.rocksdb.RocksDBHelper;

public class RocksDBHelperTest {

    @Test
    public void test() {
        RocksDBHelper rocksDBHelper = new RocksDBHelper("target/test.db");
        System.out.println(rocksDBHelper.getString("test"));
        rocksDBHelper.put("test", "testW分五个五个" + System.currentTimeMillis());
        System.out.println(rocksDBHelper.getString("test"));

        System.out.println(rocksDBHelper.getObject("testa", A.class));

        rocksDBHelper.put("testa", new A(1, "dddd王国维"));

        System.out.println(rocksDBHelper.getObject("testa", A.class));

        rocksDBHelper.close();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class A  {
        private int a;
        private String b;
    }

}
