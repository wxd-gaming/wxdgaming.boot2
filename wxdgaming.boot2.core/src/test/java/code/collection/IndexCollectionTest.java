package code.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.collection.IndexCollection;

/**
 * 索引测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-08 20:50
 **/
@Slf4j
public class IndexCollectionTest {

    @Data
    @AllArgsConstructor
    private class Usr {

        private long uid;
        private String loginName;
    }

    @Test
    public void test() {
        IndexCollection<Usr> indexCollection = new IndexCollection<>();
        indexCollection
                .registerIndex("uid", Usr::getUid)
                .registerIndex("loginName", Usr::getLoginName);
        Usr abc = new Usr(1, "abc");
        indexCollection.add(abc);

        log.info("{}", indexCollection.get("uid", 1L));
        log.info("{}", indexCollection.get("loginName", "abc"));

        indexCollection.remove(abc);

        log.info("{}", indexCollection.get("uid", 1L));
        log.info("{}", indexCollection.get("loginName", "abc"));

    }

}
