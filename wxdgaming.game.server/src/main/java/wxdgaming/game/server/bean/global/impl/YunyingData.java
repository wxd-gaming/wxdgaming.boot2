package wxdgaming.game.server.bean.global.impl;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.server.bean.global.DataBase;

import java.util.ArrayList;
import java.util.HashMap;

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
    private ArrayList<String> gmAccountSet = new ArrayList<>();
    /** 拥有gm权限的角色id */
    private ArrayList<Long> gmPlayerIdSet = new ArrayList<>();
    /** 禁止登录 */
    private HashMap<String, Long> banLogin4AccountMap = new HashMap<>();
    /** 禁止登录 */
    private HashMap<Long, Long> banLogin4RidMap = new HashMap<>();

}
