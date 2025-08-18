package update;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 技能
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-25 16:48
 **/
@Getter
@Setter
@Accessors(chain = true)
public class Skill {

    private int id;
    private int lv;
    /** 间隔 */
    private int execInterval;
    private int minHp;
    private int maxHp;
    /** 如果cd大于0表示不能执行 */
    private int cd;

}
