package wxdgaming.boot2.starter.condition;

/**
 * 条件的比较强
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-26 15:04
 **/
public interface IConditionComparator {

    String compareKey();

    boolean compare(long self, long target);

}
