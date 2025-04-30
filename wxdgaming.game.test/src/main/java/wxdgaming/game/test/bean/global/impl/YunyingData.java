package wxdgaming.game.test.bean.global.impl;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.test.bean.global.DataBase;

import java.util.HashSet;

/**
 * 运营数据
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-29 13:18
 **/
@Getter
@Setter
public class YunyingData extends DataBase {

    /** 拥有gm权限的账号 */
    private HashSet<String> gmAccountSet = new HashSet<>();
    /** 拥有gm权限的角色id */
    private HashSet<Long> gmPlayerIdSet = new HashSet<>();

}
