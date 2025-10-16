package code.collection;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.collection.RandomList;

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
        randomList.remove(2);
        System.out.println(randomList.getList());
        System.out.println(randomList.random());
    }

}
