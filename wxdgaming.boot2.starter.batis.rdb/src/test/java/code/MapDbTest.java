package code;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-20 17:14
 **/
public class MapDbTest {

    @Test
    public void putFileDB() {
        DB db = DBMaker.fileDB("target/file.db")
                .fileLockDisable()
                .make();
        ConcurrentMap map = db.hashMap("map", Serializer.STRING, Serializer.JAVA).createOrOpen();
        map.put("something", "here");
        readFileDB("map");
        readFileDB("map2");
        db.close();
    }

    @Test
    public void getFileDB() {
        try (DB db = DBMaker.fileDB("target/file.db")
                .fileLockDisable()
                .make()) {


            ConcurrentMap<String, Object> map = db.hashMap("map2", Serializer.STRING, Serializer.JAVA).createOrOpen();
            map.put("something" + System.currentTimeMillis(), new AA().setName("here"));
            readFileDB("map");
            readFileDB("map2");
        }
    }

    public void readFileDB(String cacheName) {
        DB db = DBMaker.fileDB("target/file.db")
                .readOnly()
                .make();
        ConcurrentMap<String, Object> map = db.hashMap(cacheName, Serializer.STRING, Serializer.JAVA).createOrOpen();
        String collect = map.entrySet().stream().map(v -> v.getKey() + ":" + v.getValue()).collect(Collectors.joining("&"));
        System.out.println("getFileDB:" + collect);
        db.close();
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
