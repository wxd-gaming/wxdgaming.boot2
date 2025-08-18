package wxdgaming.boot2.core.format;

import wxdgaming.boot2.core.timer.MyClock;

import java.text.DecimalFormat;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 每秒钟能产生 1亿 个ID
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2020-12-30 20:33
 */
public class StringIdFormat {

    protected static final DecimalFormat DFT = new DecimalFormat("0000000");

    protected final ReentrantLock readLock = new ReentrantLock(false);
    protected String format = "0";
    protected volatile long id = -1;
    protected volatile long MAXID = 9999999L;
    protected volatile long upTime = 0;
    protected String idHead = "";

    /**
     * 每秒钟能产生 1亿 个ID
     *
     * @return
     */
    public String getId() {
        long now = System.currentTimeMillis() / 1000;
        String tmpid = "";
        /* 这里的锁基本不耗时 */
        readLock.lock();
        try {
            if (now != upTime) {
                upTime = now;
                /*这一段相对而言，比较耗时*/
                idHead = MyClock.formatDate("yyyyMMddHHmmss");
                id = -1;
            }
            ++id;
            if (id > MAXID) {
                throw new UnsupportedOperationException("超过每秒钟创建量 " + MAXID);
            }
            tmpid = idHead + DFT.format(id);
        } finally {
            readLock.unlock();
        }
        return tmpid;
    }

}
