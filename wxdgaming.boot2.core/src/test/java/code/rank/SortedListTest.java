package code.rank;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.rank.SortedList;

import java.util.List;

public class SortedListTest {

    @Test
    public void test() {
        SortedList<Long, Integer> list = new SortedList<>();
        list.update(1L, 1);
        list.update(2L, 2);
        list.update(3L, 2);
        list.update(7L, 8);
        List<SortedList<Long, Integer>.Element> list1 = list.toSortElement();
        System.out.println(list1);
        list.update(2L, 9);
        System.out.println(list.toSortElement());
        list.update(1L, 3);
        System.out.println(list.toSortElement());
    }
}

