package wxdgaming.boot2.core.runtime;

/**
 * 执行耗时
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-06-05 10:16
 **/
public class RunTimeCost {

    public static long costMs(long startNano) {
        long l = System.nanoTime() - startNano;
        l = l / 1000000;
        return l;
    }

    private long start;

    private RunTimeCost() {}

    public void start() {
        this.start = System.nanoTime();
    }

    public long costMs() {
        return costMs(start);
    }

}
