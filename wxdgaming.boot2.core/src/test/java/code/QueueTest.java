package code;

import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class QueueTest {

    @Test
    public void q1() {
        ArrayBlockingQueue<String> queue =new ArrayBlockingQueue<>(100);
        queue.add("1");
    }

}
