package wxdgaming.game.server.script.recharge.filter;

/**
 * 充值过滤
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-16 13:47
 **/
public interface IRechargeFilter {

    int getRechargeType();

    boolean filter(int productId, int count, boolean sendNotice);

}
