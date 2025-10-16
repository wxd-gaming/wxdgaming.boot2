package code;

import org.junit.jupiter.api.*;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.lang.DiffTimeRecord;
import wxdgaming.boot2.core.rank.RankElement;
import wxdgaming.boot2.core.util.RandomUtils;
import wxdgaming.boot2.starter.batis.mapdb.HoldMap;
import wxdgaming.boot2.starter.batis.mapdb.MapDBDataHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RankByBMapDBTest {

    static final MapDBDataHelper mapDBDataHelper = new MapDBDataHelper("target/btreerank.db");
    static final HoldMap holdMap = mapDBDataHelper.bMap("lv-rank");

    @Order(1)
    @Test
    public void aputRank() {

        holdMap.clear();

        for (int i = 0; i < 100000; i++) {
            String k = String.valueOf(RandomUtils.random(1, Long.MAX_VALUE));
            RankElement rankElement = new RankElement().setKey(k);
            rankElement.setTimestamp(System.nanoTime());
            rankElement.setScore(RandomUtils.random(1, 5000));
            holdMap.put(k, rankElement);
        }

    }

    @Order(2)
    @RepeatedTest(10)
    public void b1treeSet() {
        DiffTimeRecord diffTime = DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        diffTime.marker("读取耗时");
        TreeSet<RankElement> rankElements = new TreeSet<>();
        for (Object value : objects) {
            RankElement rankElement = (RankElement) value;
            rankElements.add(rankElement);
        }
        diffTime.marker("排序耗时" + holdMap.size());
        List<RankElement> list = rankElements.stream().limit(20).toList();

        System.out.println(diffTime);
        System.out.println("==============================");
        //        float v = diffTime.diffMs5();
        //        for (RankScore rankScore : list) {
        //            System.out.println(rankScore);
        //        }
    }

    @Order(2)
    @RepeatedTest(10)
    public void b2skipSet() {
        DiffTimeRecord diffTime = DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        diffTime.marker("读取耗时");
        ConcurrentSkipListSet<RankElement> rankElements = new ConcurrentSkipListSet<>();
        for (Object value : objects) {
            RankElement rankElement = (RankElement) value;
            rankElements.add(rankElement);
        }
        diffTime.marker("排序耗时" + holdMap.size());
        List<RankElement> list = rankElements.stream().limit(20).toList();
        System.out.println(diffTime);
        System.out.println("==============================");
        //        float v = diffTime.diffMs5();
        //        for (RankScore rankScore : list) {
        //            System.out.println(rankScore);
        //        }
    }

    @Order(3)
    @RepeatedTest(10)
    public void clistSort() {
        DiffTimeRecord diffTime = DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        diffTime.marker("读取耗时");
        List<RankElement> list = objects.stream().map(value -> (RankElement) value).sorted().limit(20).toList();
        diffTime.marker("排序耗时" + holdMap.size());
        System.out.println(diffTime);
        System.out.println("==============================");
        //        float v = diffTime.diffMs5();
        //        for (RankScore rankScore : list) {
        //            System.out.println(rankScore);
        //        }
    }

    @Order(4)
    @RepeatedTest(10)
    public void darraySort() {
        DiffTimeRecord diffTime = DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        diffTime.marker("读取耗时");
        RankElement[] array = objects.toArray(new RankElement[0]);
        Arrays.sort(array);
        diffTime.marker("排序耗时" + array.length);
        System.out.println(diffTime);
        System.out.println("==============================");

        //        for (RankScore rankScore : list) {
        //            System.out.println(rankScore);
        //        }
    }

    @Order(99994)
    @Test
    public void memery() {
        DiffTimeRecord diffTime = DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        String string = Data2Size.totalSizes0(objects);
        System.out.println("长度：" + objects.size() + "内存占用：" + string);
    }

}
