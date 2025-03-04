package run;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.js.JSContext;

import java.util.ArrayList;
import java.util.List;

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
        Actor hello = new Actor(999997203685477580L, "hello");
        hello.getStrings().add("world");
        build.invokeMember("ct", "print2", hello);
        build.eval("jlog.debug('hello world;')");
        build.eval("jlog.debug('{}',1)");
        build.eval("jlog.info('hello world;')");
    }

    @Getter
    @Setter
    public static class Actor extends ObjectBase {

        public long uid;
        public String name;
        public List<String> strings = new ArrayList<>();

        public Actor(long uid, String name) {
            this.uid = uid;
            this.name = name;
        }

        public String getName() {
            System.out.println("---------");
            return name;
        }


    }

}
