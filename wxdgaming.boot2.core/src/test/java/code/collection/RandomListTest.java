package code.collection;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.collection.RandomList;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentRandomList;

public class RandomListTest {

    @Test
    public void t1() {
        RandomList<Integer> randomList = new RandomList<>();
        randomList.add(1);
        randomList.add(3);
        randomList.add(2);
        randomList.add(4);
        System.out.println(randomList.getList());
        System.out.println(randomList.random());
        randomList.remove(3);
        System.out.println(randomList.getList());
        System.out.println(randomList.random());
    }

    @Test
    public void t2() {
        ConcurrentRandomList<Integer> randomList = new ConcurrentRandomList<>();
        randomList.add(1);
        randomList.add(3);
        randomList.add(2);
        randomList.add(4);
        System.out.println(randomList.getList());
        System.out.println(randomList.random());
        randomList.remove(4);
        System.out.println(randomList.getList());
        System.out.println(randomList.random());
    }

}
