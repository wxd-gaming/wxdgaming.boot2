package code.sort;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.lang.DiffTime;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.ArrayList;
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


    @Test
    public void arraySort() {
        ArrayList<SortBean> arrayList = null;
        for (int k = 0; k < 4; k++) {
            DiffTime diffTime = new DiffTime();
            arrayList = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                SortBean sortBean = new SortBean();
                sortBean.setKey(String.valueOf(i));
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                arrayList.add(sortBean);
            }
            arrayList.sort(null);
            float v = diffTime.diffMs5();
            System.out.println("array add Sort = " + v);
        }

        for (int k = 0; k < 4; k++) {
            List<SortBean> sortBeans = List.copyOf(arrayList);
            DiffTime diffTime = new DiffTime();
            for (SortBean sortBean : sortBeans) {
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                arrayList.sort(null);
            }
            float v = diffTime.diffMs5();
            System.out.println("array update all Sort = " + v);
        }


        for (int k = 0; k < 4; k++) {
            DiffTime diffTime = new DiffTime();
            SortBean sortBean = RandomUtils.randomItem(arrayList);
            sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            arrayList.sort(null);
            float v = diffTime.diffMs5();
            System.out.println("array update one Sort = " + v);
        }
    }


    @Test
    public void setSort() {
        TreeSet<SortBean> treeSet = null;
        for (int k = 0; k < 4; k++) {
            DiffTime diffTime = new DiffTime();
            treeSet = new TreeSet<>();
            for (int i = 0; i < count; i++) {
                SortBean sortBean = new SortBean();
                sortBean.setKey(String.valueOf(i));
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                treeSet.add(sortBean);
            }
            float v = diffTime.diffMs5();
            System.out.println("set add Sort = " + v);
        }

        for (int k = 0; k < 4; k++) {
            List<SortBean> sortBeans = List.copyOf(treeSet);
            DiffTime diffTime = new DiffTime();
            for (SortBean sortBean : sortBeans) {
                treeSet.remove(sortBean);
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                treeSet.add(sortBean);
            }
            float v = diffTime.diffMs5();
            System.out.println("set update all Sort = " + v);
        }

        for (int k = 0; k < 4; k++) {
            DiffTime diffTime = new DiffTime();
            SortBean sortBean = RandomUtils.randomItem(treeSet);
            treeSet.remove(sortBean);
            sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            treeSet.add(sortBean);
            float v = diffTime.diffMs5();
            System.out.println("set update one Sort = " + v);
        }
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
