package code.queue;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

public class CopyTest {

    @Test
    public void c1() {
        LinkedBlockingQueue<Node> queue = new LinkedBlockingQueue<>();
        queue.add(new Node().setVal(1));
        queue.add(new Node().setVal(2).setSub(new NodeSub().setType("test")));
        queue.add(new Node().setVal(3));
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public class Node {
        private int val;
        private NodeSub sub;


        @JSONField(serialize = false)
        public int getAAA() {
            return 1;
        }
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class NodeSub {
        private String type;
    }

}
