package wxdgaming.boot2.starter.lua.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 数据结构
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 13:20
 **/
@Getter
@Setter
public abstract class LuaData {

    /** true 表示开启心跳处理 */
    private boolean openHeart = false;
    /** day 变量。到凌晨就直接丢弃了 */
    private VarTable dDataTable = new VarTable();
    /** 永久存储 */
    private VarTable varDataTable = new VarTable();

}
