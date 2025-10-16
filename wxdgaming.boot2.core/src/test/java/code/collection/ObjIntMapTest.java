package code.collection;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.collection.ObjIntMap;
import wxdgaming.boot2.core.json.FastJsonUtil;

@Getter
@Setter
public class ObjIntMapTest {

    ObjIntMap<E> objIntMap = new ObjIntMap<>();

    @Test
    public void t1() {

        objIntMap.put(new E("a"), 1);
        objIntMap.put(new E("b"), 2);
        int i = objIntMap.putCount(new E("c"), 3);
        System.out.println(i);
        String json = FastJsonUtil.toJSONStringAsWriteType(this);
        System.out.println(json);
        ObjIntMapTest parse = FastJsonUtil.parseSupportAutoType(json, this.getClass());
        System.out.println(FastJsonUtil.toJSONString(parse));
    }

    @Getter
    @Setter
    public static class E implements Comparable<E> {

        private String e;

        public E() {
        }

        public E(String e) {
            this.e = e;
        }

        @Override public int compareTo(E o) {
            return e.compareTo(o.e);
        }
    }
}
