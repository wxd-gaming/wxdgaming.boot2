package code.collection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentMappingData;

public class ConcurrentMappingDataTest {

    @Test
    public void m1() {

        ConcurrentMappingData<Long, String, TestUser> concurrentMappingData = new ConcurrentMappingData<>(TestUser::getUid, TestUser::getName);
        TestUser admin = new TestUser(1, "admin");
        concurrentMappingData.add(admin);
        System.out.println(concurrentMappingData.getK1().get(1L));
        System.out.println(concurrentMappingData.getK2().get("admin"));

    }

    @Getter
    @Setter
    @ToString
    public static class TestUser {

        private long uid;
        private String name;

        public TestUser(long uid, String name) {
            this.uid = uid;
            this.name = name;
        }

    }
}
