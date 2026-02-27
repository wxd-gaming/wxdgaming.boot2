package wxdgaming.boot2.starter.lua.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * lua参数
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-12-30 11:30
 **/
@Getter
@Setter
public class LuaMap extends LuaData {

    public long uid;
    public int cfgId;


    private final ConcurrentHashMap<Long, LuaData> objects = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Long, LuaData> player = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, LuaData> monster = new ConcurrentHashMap<>();

    public LuaMap(long uid, int cfgId) {
        this.uid = uid;
        this.cfgId = cfgId;
    }

    @Override public String toString() {
        return "LuaMap{uid=%d, cfgId=%d}".formatted(uid, cfgId);
    }

}
