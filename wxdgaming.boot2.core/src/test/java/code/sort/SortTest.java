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
        for (int k = 0; k < 4; k++) {
            DiffTimeRecord diffTime = newDiffTimeRecord();
            arrayList = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                SortBean sortBean = new SortBean();
                sortBean.setKey(String.valueOf(i));
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                arrayList.add(sortBean);
            }
            arrayList.sort(null);
            DiffTimeRecord.RecordTime recordTime = diffTime.totalInterval();
            System.out.println("array add Sort = " + recordTime.toString());
        }

        for (int k = 0; k < 4; k++) {
            List<SortBean> sortBeans = List.copyOf(arrayList);
            DiffTimeRecord diffTime = newDiffTimeRecord();
            for (SortBean sortBean : sortBeans) {
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                arrayList.sort(null);
            }
            DiffTimeRecord.RecordTime recordTime = diffTime.totalInterval();
            System.out.println("array update all Sort = " + recordTime.toString());
        }


        for (int k = 0; k < 4; k++) {
            DiffTimeRecord diffTime = newDiffTimeRecord();
            SortBean sortBean = RandomUtils.randomItem(arrayList);
            sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            arrayList.sort(null);
            DiffTimeRecord.RecordTime recordTime = diffTime.totalInterval();
            System.out.println("array update one Sort = " + recordTime.toString());
        }
    }


    @Test
    public void setSort() {
        TreeSet<SortBean> treeSet = null;
        for (int k = 0; k < 4; k++) {
            DiffTimeRecord diffTime = newDiffTimeRecord();
            treeSet = new TreeSet<>();
            for (int i = 0; i < count; i++) {
                SortBean sortBean = new SortBean();
                sortBean.setKey(String.valueOf(i));
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                treeSet.add(sortBean);
            }
            DiffTimeRecord.RecordTime recordTime = diffTime.totalInterval();
            System.out.println("set add Sort = " + recordTime.toString());
        }

        for (int k = 0; k < 4; k++) {
            List<SortBean> sortBeans = List.copyOf(treeSet);
            DiffTimeRecord diffTime = newDiffTimeRecord();
            for (SortBean sortBean : sortBeans) {
                treeSet.remove(sortBean);
                sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
                treeSet.add(sortBean);
            }
            DiffTimeRecord.RecordTime recordTime = diffTime.totalInterval();
            System.out.println("set update all Sort = " + recordTime.toString());
        }

        for (int k = 0; k < 4; k++) {
            DiffTimeRecord diffTime = newDiffTimeRecord();
            SortBean sortBean = RandomUtils.randomItem(treeSet);
            treeSet.remove(sortBean);
            sortBean.setOverTime(System.currentTimeMillis() + RandomUtils.random(100, 10000));
            treeSet.add(sortBean);
            DiffTimeRecord.RecordTime recordTime = diffTime.totalInterval();
            System.out.println("set update one Sort = " + recordTime.toString());
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
