package wxdgaming.boot2.core.threading;


/** 取消 */
public interface Job {

    /** 获取名字 */
    String names();

    /** 取消 */
    boolean cancel();

}
