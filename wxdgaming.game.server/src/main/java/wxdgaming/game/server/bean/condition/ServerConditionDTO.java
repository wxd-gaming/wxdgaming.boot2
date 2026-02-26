package wxdgaming.game.server.bean.condition;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.condition.ConditionDTO;
import wxdgaming.game.server.bean.count.CountMap;
import wxdgaming.game.server.bean.role.Player;

/**
 * server 条件检测 参数传递
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-26 19:39
 **/
@Getter
@Setter
public class ServerConditionDTO extends ConditionDTO {

    private Player player;
    private CountMap countMap;

}
