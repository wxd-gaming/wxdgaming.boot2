package code;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.util.StopWatch;
import wxdgaming.boot2.core.util.DumpUtil;
import wxdgaming.boot2.core.util.RandomUtils;
import wxdgaming.boot2.starter.batis.mapdb.HoldMap;
import wxdgaming.boot2.starter.batis.mapdb.MapDBDataHelper;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-20 17:14
 **/
public class MapDbTest2 {

    @Test
    public void putFileDB() {
        try (DB db = DBMaker.fileDB("target/file00.db")
                .fileMmapEnableIfSupported() // 启用内存映射（提升读性能）
                .fileMmapPreclearDisable()   // 禁用预清理（避免写时阻塞）
                .cleanerHackEnable()         // 启用清理黑客（提升 mmap 关闭可靠性）
                .make()) {
            ConcurrentMap map = db.hashMap("map", Serializer.STRING, Serializer.JAVA).createOrOpen();
            map.put("something" + System.currentTimeMillis(), "here");
            readFileDB(db, "map");
            readFileDB(db, "map2");
            readFileDB(db, "map3");
        }
    }

    @Test
    public void getFileDB() {
        try (DB db = DBMaker.fileDB("target/filet11.db")
                .fileMmapEnableIfSupported() // 启用内存映射（提升读性能）
                .fileMmapPreclearDisable()   // 禁用预清理（避免写时阻塞）
                .cleanerHackEnable()         // 启用清理黑客（提升 mmap 关闭可靠性）
                .make()) {

            ConcurrentMap<String, Object> map = db.hashMap("map2", Serializer.STRING, Serializer.JAVA).createOrOpen();
            map.put("something" + System.currentTimeMillis(), new AA().setName("here"));

            Thread.ofPlatform().start(() -> {
                readFileDB("map");
                readFileDB("map2");
            });

            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
        }
    }

    public void readFileDB(String cacheName) {
        try (DB db = DBMaker.fileDB("target/filet22.db")
                .readOnly()
                .fileMmapEnableIfSupported() // 启用内存映射（提升读性能）
                .make()) {
            readFileDB(db, cacheName);
        }
    }

    public void readFileDB(DB db, String cacheName) {
        boolean exists = db.exists(cacheName);
        if (!exists) {
            System.out.println("getFileDB:cacheName:" + cacheName + " not exists");
            return;
        }
        StopWatch diffTime = new StopWatch();
        ConcurrentMap<String, Object> map = db.hashMap(cacheName, Serializer.STRING, Serializer.JAVA).open();
        diffTime.start("读取耗时");
        String collect = Joiner.on("&").withKeyValueSeparator(":").join(map);
        diffTime.start("打印耗时");
        System.out.println(diffTime + "\n" + collect);
    }


    @Test
    public void putFileDBSize() {
        File file = new File("target/file444.db");
        MapDBDataHelper db = new MapDBDataHelper(file);
        try {
            System.out.println("start");
            //            for (int i = 0; i < 1000; i++) {
            //                DiffTimeRecord diffTime = DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
            //                String cacheName = "map" + RandomUtils.random(1000);
            //                HoldMap holdMap = db.hMap(cacheName);
            //                System.out.println(String.format("打开耗时 %s ms, name=%s", diffTime.diffMs5AndReset(), cacheName));
            //                for (int k = 0; k < 1000; k++) {
            //                    holdMap.put("something " + k, new AA().setName("aa" + System.currentTimeMillis()));
            //                }
            //                System.out.println(String.format("写入耗时 %s ms, name=%s", diffTime.diffMs5AndReset(), cacheName));
            //            }
            System.out.println("=====================");
            readFileDB(db, "map" + RandomUtils.random(100), "something " + RandomUtils.random(1000));
            readFileDB(db, "map" + RandomUtils.random(100), "something " + RandomUtils.random(1000));
            readFileDB(db, "map" + RandomUtils.random(100), "something " + RandomUtils.random(1000));

            System.out.println("=====================");
            System.gc();
            System.gc();
            StringBuilder stringBuilder = new StringBuilder();
            DumpUtil.freeMemory(stringBuilder);
            System.out.println(stringBuilder.toString());
            Set<Object> set1 = db.hashSet("set1");
            set1.add("something " + RandomUtils.random(10));
        } finally {
            db.stop(null);
        }
    }

    public void readFileDB(MapDBDataHelper db, String cacheName, String key) {
        boolean exists = db.exists(cacheName);
        if (!exists) {
            System.out.println("getFileDB:cacheName:" + cacheName + " not exists");
            return;
        }
        StopWatch diffTime = new StopWatch();
        HoldMap holdMap = db.hMap(cacheName);
        diffTime.start("打开耗时");
        Object o = holdMap.get(key);
        diffTime.start(String.format("读取, key=%s, valueType=%s, value=%s", key, o.getClass().getSimpleName(), o));
        Object o1 = holdMap.get(key);
        diffTime.start(String.format("读取, key=%s, valueType=%s, value=%s", key, o1.getClass().getSimpleName(), o1));
        String jsonString = JSON.toJSONString(holdMap.getHold());
        diffTime.start("打印耗时");
        System.out.println(diffTime + "\n" + jsonString);
    }

    @Test
    public void m1() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        System.out.println(stringStringHashMap.putIfAbsent("1", "1"));
        System.out.println(stringStringHashMap.putIfAbsent("1", "1"));
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class AA implements Serializable {

        @Serial private static final long serialVersionUID = 1L;

        private String name;

        @Override public String toString() {
            return "AA{name='%s'}".formatted(name);
        }
    }

}
