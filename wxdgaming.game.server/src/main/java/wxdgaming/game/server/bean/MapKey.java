package wxdgaming.game.server.bean;

import com.alibaba.fastjson2.annotation.JSONCreator;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 地图索引
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-15 17:22
 **/
@Getter
public class MapKey extends ObjectBase {

    private final int mapId;
    private final int mapCfgId;
    private final int line;

    @JSONCreator
    public MapKey(@JSONField(name = "mapId") int mapId,
                  @JSONField(name = "mapCfgId") int mapCfgId,
                  @JSONField(name = "line") int line) {
        this.mapId = mapId;
        this.mapCfgId = mapCfgId;
        this.line = line;
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        MapKey mapKey = (MapKey) o;
        return getMapCfgId() == mapKey.getMapCfgId() && getLine() == mapKey.getLine();
    }

    @Override public int hashCode() {
        int result = getMapCfgId();
        result = 31 * result + getLine();
        return result;
    }
}
