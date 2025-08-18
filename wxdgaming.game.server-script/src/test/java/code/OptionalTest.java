package code;

import org.junit.Test;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;

/**
 * 测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-23 16:52
 **/
public class OptionalTest {

    @Test
    public void t1(){
        ConcurrentTable<Integer, Integer, Integer> table = new ConcurrentTable<>();
        table.put(1, 1, 1);
        int sum = table.opt(2).stream().flatMap(v -> v.values().stream()).mapToInt(v -> v).sum();
        System.out.println(sum);
    }

}
