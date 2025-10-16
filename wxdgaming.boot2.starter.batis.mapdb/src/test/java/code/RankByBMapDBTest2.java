package code;

import org.junit.jupiter.api.*;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.lang.DiffTimeRecord;
import wxdgaming.boot2.core.rank.RankElement;
import wxdgaming.boot2.core.util.RandomUtils;
import wxdgaming.boot2.starter.batis.mapdb.HoldMap;
import wxdgaming.boot2.starter.batis.mapdb.MapDBDataHelper;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RankByBMapDBTest2 {

    static final MapDBDataHelper mapDBDataHelper = new MapDBDataHelper("target/btreerank2.db");
    static final HoldMap holdMap = mapDBDataHelper.bMap("lv-rank");

    @Test
    @Order(1)
    public void aputRank() {


        holdMap.clear();

        for (int i = 0; i < 100000; i++) {
            String k = String.valueOf(RandomUtils.random(1, Long.MAX_VALUE));
            holdMap.put(k, RandomUtils.random(1, Long.MAX_VALUE));
        }

    }

    @RepeatedTest(10)
    @Order(2)
    public void b1treeSet() {
        DiffTimeRecord diffTime = DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        diffTime.marker("读取");
        TreeSet<RankElement> rankElements = new TreeSet<>();
        for (Object value : objects) {
            RankElement rankElement = (RankElement) value;
            rankElements.add(rankElement);
        }
        diffTime.marker("排序" + holdMap.size());
        List<RankElement> list = rankElements.stream().limit(20).toList();

        System.out.println(diffTime);
        System.out.println("==============================");
        //        float v = diffTime.diffMs5();
        //        for (RankScore rankScore : list) {
        //            System.out.println(rankScore);
        //        }
    }

    @RepeatedTest(10)
    @Order(2)
    public void b2skipSet() {
        DiffTimeRecord diffTime = DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        diffTime.marker("读取");
        ConcurrentSkipListSet<RankElement> rankElements = new ConcurrentSkipListSet<>();
        for (Object value : objects) {
            RankElement rankElement = (RankElement) value;
            rankElements.add(rankElement);
        }
        diffTime.marker("排序" + holdMap.size());
        List<RankElement> list = rankElements.stream().limit(20).toList();

        System.out.println(diffTime);
        System.out.println("==============================");
        //        float v = diffTime.diffMs5();
        //        for (RankScore rankScore : list) {
        //            System.out.println(rankScore);
        //        }
    }

    @RepeatedTest(10)
    @Order(3)
    public void clistSort() {
        DiffTimeRecord diffTime = DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        diffTime.marker("读取");
        List<RankElement> list = objects.stream().map(value -> (RankElement) value).sorted().limit(20).toList();
        diffTime.marker("排序 " + holdMap.size());
        System.out.println(diffTime);
        System.out.println("==============================");
        //        float v = diffTime.diffMs5();
        //        for (RankScore rankScore : list) {
        //            System.out.println(rankScore);
        //        }
    }

    @RepeatedTest(10)
    @Order(4)
    public void darraySort() {
        DiffTimeRecord diffTime = DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        diffTime.marker("读取");
        RankElement[] array = objects.toArray(new RankElement[0]);
        Arrays.sort(array);
        diffTime.marker("排序 " + array.length);
        System.out.println(diffTime);
        System.out.println("==============================");

        //        for (RankScore rankScore : list) {
        //            System.out.println(rankScore);
        //        }
    }

    public void memery() {
        DiffTimeRecord diffTime = DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
        Collection<Object> values = holdMap.values();
        String string = Data2Size.totalSizes0(values);
        System.out.println("内存占用：" + string);
    }

}
