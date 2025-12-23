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

        rocksDBHelper.put("testa", new A("dddd王国维", "dd", 2, 1, new B("cc",2)));

        System.out.println(rocksDBHelper.getObject("testa", A.class));

        rocksDBHelper.close();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class A  {
        private String c;
        private String b;
        private int i2;
        private int a;
        private B b2;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class B {
        private String c;
        private int i2;
    }

}
