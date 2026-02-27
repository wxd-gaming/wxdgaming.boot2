package wxdgaming.boot2.starter.lua.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据结构
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 13:20
 **/
@Getter
@Setter
public class LuaData {

    /** true 表示开启心跳处理 */
    private boolean openHeart = false;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> data = new ConcurrentHashMap<>();

}
