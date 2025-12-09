package code.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.collection.IndexMultipart1ToNCollection;

public class IndexMultipart1ToNCollectionTest {

    @Data
    @AllArgsConstructor
    public static class BuffCfg {
        private String uid;
        private int id;
        private int lv;
        private String name;
    }

    @Test
    public void test() {
        IndexMultipart1ToNCollection<BuffCfg> collection = new IndexMultipart1ToNCollection<>();

        collection
                .registerIndex("id", BuffCfg::getId)
                .registerIndex("id-lv", BuffCfg::getId, BuffCfg::getLv);

        BuffCfg buffCfg = new BuffCfg("1-1", 1, 1, "1");
        collection.add(buffCfg);
        collection.add(new BuffCfg("1-2", 1, 2, "2"));

        System.out.println("id=1的所有数据,  " + collection.get("id", 1));
        System.out.println("---------------------------");
        System.out.println("id=1, lv=1的所有数据,  " +collection.get("id-lv", 1, 1));
        System.out.println("id=1, lv=2的所有数据,  " +collection.get("id-lv", 1, 2));
        System.out.println("---------------------------");
        collection.remove(buffCfg);

        System.out.println("id=1的所有数据,  " +collection.get("id", 1));
        System.out.println("---------------------------");
        System.out.println("id=1, lv=1的所有数据,  " +collection.get("id-lv", 1, 1));
        System.out.println("id=1, lv=2的所有数据,  " +collection.get("id-lv", 1, 2));

    }

}
