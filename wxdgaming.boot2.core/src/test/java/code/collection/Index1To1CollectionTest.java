package code.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.collection.Index1To1Collection;

/**
 * 索引测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-08 20:50
 **/
@Slf4j
public class Index1To1CollectionTest {

    @Data
    @AllArgsConstructor
    private class Usr {

        private long uid;
        private String loginName;
    }

    @Test
    public void test() {
        Index1To1Collection<Usr> index1To1Collection = new Index1To1Collection<>();
        index1To1Collection
                .registerIndex("uid", Usr::getUid)
                .registerIndex("loginName", Usr::getLoginName);
        Usr abc = new Usr(1, "abc");
        index1To1Collection.add(abc);

        log.info("{}", index1To1Collection.get("uid", 1L));
        log.info("{}", index1To1Collection.get("loginName", "abc"));

        index1To1Collection.remove(abc);

        log.info("{}", index1To1Collection.get("uid", 1L));
        log.info("{}", index1To1Collection.get("loginName", "abc"));

    }

}
