package wxdgaming.game.server.bean;

/**
 * 地图特性，比如火墙，闪电等
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-23 19:16
 */
public class MapEffect extends MapMonster {

    public MapEffect() {
        this.setMapObjectType(MapObjectType.Effect);
    }

}
