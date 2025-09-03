package wxdgaming.game.server.bean.achieve;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;

/**
 * 成就容器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-03 15:02
 **/
@Getter
@Setter
public class AchievePack extends ObjectBase {

    private HashMap<Integer, AchieveProgress> achieveMap = new HashMap<>();

}
