package code;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import wxdgaming.boot2.core.lang.DiffTime;
import wxdgaming.boot2.core.util.DumpUtil;
import wxdgaming.boot2.core.util.RandomUtils;
import wxdgaming.boot2.starter.batis.mapdb.MapDBDataHelper;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-20 17:14
 **/
public class MapDbTest {

    @Test
    public void putFileDB() {
        try (DB db = DBMaker.fileDB("target/file.db")
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
        try (DB db = DBMaker.fileDB("target/filet.db")
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
        try (DB db = DBMaker.fileDB("target/filet.db")
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
        DiffTime diffTime = new DiffTime();
        ConcurrentMap<String, Object> map = db.hashMap(cacheName, Serializer.STRING, Serializer.JAVA).open();
        System.out.println(String.format("读取耗时 %s ms", diffTime.diffMs5AndReset()));
        String collect = map.entrySet().stream().map(v -> v.getKey() + ":" + v.getValue()).collect(Collectors.joining("&"));
        System.out.println(String.format("打印耗时 %s ms, getFileDB:%s", diffTime.diffMs5AndReset(), collect));
    }


    @Test
    public void putFileDBSize() {
        File file = new File("target/file.db");
        try (MapDBDataHelper db = new MapDBDataHelper(file)) {
            System.out.println("start");
            for (int i = 0; i < 1000; i++) {
                DiffTime diffTime = new DiffTime();
                String name = "map" + RandomUtils.random(1000);
                System.out.println(String.format("打开耗时 %s ms, name=%s", diffTime.diffMs5AndReset(), name));
                for (int k = 0; k < 1000; k++) {
                    db.hTreeMapSet(name, "something " + k, new AA().setName("aa" + System.currentTimeMillis()).toString());
                }
                System.out.println(String.format("写入耗时 %s ms, name=%s", diffTime.diffMs5AndReset(), name));
            }
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
        }
    }

    public void readFileDB(MapDBDataHelper db, String cacheName, String key) {
        boolean exists = db.exists(cacheName);
        if (!exists) {
            System.out.println("getFileDB:cacheName:" + cacheName + " not exists");
            return;
        }
        DiffTime diffTime = new DiffTime();
        System.out.println(String.format("打开耗时 %s ms", diffTime.diffMs5AndReset()));
        diffTime.reset();
        Object o = db.hTreeMapGet(cacheName, key);
        System.out.println(String.format("读取耗时 %s ms, valueType=%s, value=%s", diffTime.diffMs5AndReset(), o.getClass().getSimpleName(), o));
        diffTime.reset();
        Object o1 = db.hTreeMapGet(cacheName, key);
        System.out.println(String.format("读取耗时 %s ms, valueType=%s, value=%s", diffTime.diffMs5AndReset(), o1.getClass().getSimpleName(), o1));
        diffTime.reset();
        String collect = db.hTreeMap(cacheName).entrySet().stream().map(v -> v.getKey() + ":" + v.getValue()).collect(Collectors.joining("&"));
        System.out.println(String.format("打印耗时 %s ms, getFileDB:%s", diffTime.diffMs5AndReset(), collect));
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
