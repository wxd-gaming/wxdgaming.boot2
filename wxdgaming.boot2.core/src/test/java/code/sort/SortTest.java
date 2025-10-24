package code.sort;

import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * 排序测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-26 09:45
 **/
public class SortTest {

    static int count = 10000;

    public StopWatch newDiffTimeRecord() {
        return new StopWatch("测试");
    }

    @Test
    public void arraySort() {
        SortBean[] arrayList = new SortBean[count];
        for (int i = 0; i < count; i++) {
            SortBean sortBean = new SortBean();
            sortBean.setKey(String.valueOf(i));
            sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            arrayList[i] = sortBean;
        }

        StopWatch diffTime = newDiffTimeRecord();
        for (int k = 0; k < 4; k++) {
            SortBean[] sortBeans = Arrays.copyOf(arrayList, arrayList.length);
            Arrays.sort(sortBeans);
            diffTime.start("array add Sort");
        }

        for (int k = 0; k < 4; k++) {
            SortBean[] sortBeans = Arrays.copyOf(arrayList, arrayList.length);
            for (SortBean sortBean : sortBeans) {
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            }
            Arrays.sort(sortBeans);
            diffTime.start("array update all Sort");
        }

        SortBean[] sortBeans = Arrays.copyOf(arrayList, arrayList.length);
        for (int k = 0; k < 4; k++) {
            SortBean sortBean = RandomUtils.random(sortBeans);
            sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            Arrays.sort(sortBeans);
            diffTime.start("array update one Sort");
        }

        System.out.println(diffTime.prettyPrint());

    }

    @Test
    public void listSort() {
        ArrayList<SortBean> arrayList = null;

        arrayList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            SortBean sortBean = new SortBean();
            sortBean.setKey(String.valueOf(i));
            sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            arrayList.add(sortBean);
        }

        StopWatch diffTime = newDiffTimeRecord();
        for (int k = 0; k < 4; k++) {
            arrayList.sort(null);
            diffTime.start("list add Sort");
        }

        for (int k = 0; k < 4; k++) {
            List<SortBean> sortBeans = List.copyOf(arrayList);
            for (SortBean sortBean : sortBeans) {
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            }
            arrayList.sort(null);
            diffTime.start("list update all Sort");
        }

        for (int k = 0; k < 4; k++) {
            SortBean sortBean = RandomUtils.randomItem(arrayList);
            sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            arrayList.sort(null);
            diffTime.start("list update one Sort");
        }

        System.out.println(diffTime.prettyPrint());

    }


    @Test
    public void setSort() {
        TreeSet<SortBean> treeSet = null;
        StopWatch diffTime = newDiffTimeRecord();
        for (int k = 0; k < 4; k++) {
            treeSet = new TreeSet<>();
            for (int i = 0; i < count; i++) {
                SortBean sortBean = new SortBean();
                sortBean.setKey(String.valueOf(i));
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                treeSet.add(sortBean);
            }
            diffTime.start("set add Sort");
        }

        for (int k = 0; k < 4; k++) {
            List<SortBean> sortBeans = List.copyOf(treeSet);
            for (SortBean sortBean : sortBeans) {
                treeSet.remove(sortBean);
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                treeSet.add(sortBean);
            }
            diffTime.start("set update all Sort");
        }

        for (int k = 0; k < 4; k++) {
            SortBean sortBean = RandomUtils.randomItem(treeSet);
            treeSet.remove(sortBean);
            sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            treeSet.add(sortBean);
            diffTime.start("set update one Sort");
        }
        System.out.println(diffTime.prettyPrint());
    }

    @Test
    public void h1() {
        SortBean sortBean = new SortBean();
        System.out.println(sortBean.hashCode());
        sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
        System.out.println(sortBean.hashCode());
        sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
        System.out.println(sortBean.hashCode());
    }

}
