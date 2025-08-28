package code.sort;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.lang.DiffTimeRecord;
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

    public DiffTimeRecord newDiffTimeRecord() {
        return DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
    }

    @Test
    public void arraySort() {
        ArrayList<SortBean> arrayList = null;
        DiffTimeRecord diffTime = newDiffTimeRecord();
        for (int k = 0; k < 4; k++) {
            arrayList = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                SortBean sortBean = new SortBean();
                sortBean.setKey(String.valueOf(i));
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                arrayList.add(sortBean);
            }
            arrayList.sort(null);
            diffTime.marker("array add Sort");
        }

        for (int k = 0; k < 4; k++) {
            List<SortBean> sortBeans = List.copyOf(arrayList);
            for (SortBean sortBean : sortBeans) {
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                arrayList.sort(null);
            }
            diffTime.marker("array update all Sort");
        }


        for (int k = 0; k < 4; k++) {
            SortBean sortBean = RandomUtils.randomItem(arrayList);
            sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            arrayList.sort(null);
            diffTime.marker("array update one Sort");
        }

        System.out.println(diffTime.buildString());
        System.out.println("===================================");
        System.out.println(diffTime.buildString4());

    }


    @Test
    public void setSort() {
        TreeSet<SortBean> treeSet = null;
        DiffTimeRecord diffTime = newDiffTimeRecord();
        for (int k = 0; k < 4; k++) {
            treeSet = new TreeSet<>();
            for (int i = 0; i < count; i++) {
                SortBean sortBean = new SortBean();
                sortBean.setKey(String.valueOf(i));
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                treeSet.add(sortBean);
            }
            diffTime.marker("set add Sort");
        }

        for (int k = 0; k < 4; k++) {
            List<SortBean> sortBeans = List.copyOf(treeSet);
            for (SortBean sortBean : sortBeans) {
                treeSet.remove(sortBean);
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                treeSet.add(sortBean);
            }
            diffTime.marker("set update all Sort");
        }

        for (int k = 0; k < 4; k++) {
            SortBean sortBean = RandomUtils.randomItem(treeSet);
            treeSet.remove(sortBean);
            sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            treeSet.add(sortBean);
            diffTime.marker("set update one Sort");
        }
        System.out.println(diffTime.buildString4());
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
