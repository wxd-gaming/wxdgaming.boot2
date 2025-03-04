package run;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.js.JSContext;

/**
 * 测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-04 09:21
 **/
public class JSTest {

    public static void main(String[] args) {
        JSContext build = JSContext.build();
        build.evalFile("ClassTest.js");
        build.eval("var ct=new ClassTest();");
        build.eval("ct.print();");
        build.invokeMember("ct", "print2", new Actor(999997203685477580L, "hello"));
        build.eval("jlog.debug('hello world;')");
        build.eval("jlog.info('hello world;')");
    }

    @Getter
    @Setter
    public static class Actor {

        public long uid;
        public String name;

        public Actor(long uid, String name) {
            this.uid = uid;
            this.name = name;
        }

        public String getName() {
            System.out.println("---------");
            return name;
        }

        @Override public String toString() {
            return "Actor{" +
                   "uid=" + uid +
                   ", name='" + name + '\'' +
                   '}';
        }
    }

}
