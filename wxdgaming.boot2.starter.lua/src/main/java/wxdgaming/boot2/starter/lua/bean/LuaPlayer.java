package wxdgaming.boot2.starter.lua.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-12-09 10:15
 **/
@Getter
@Setter
public class LuaPlayer extends LuaData {

    public Long uid;
    public String name;
    public int lv = 99;

    public LuaPlayer() {
        this.setOpenHeart(true);
    }

    public LuaPlayer(Long uid, String name) {
        this.setOpenHeart(true);
        this.uid = uid;
        this.name = name;
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        LuaPlayer luaPlayer = (LuaPlayer) o;
        return Objects.equals(getUid(), luaPlayer.getUid());
    }

    @Override public int hashCode() {
        return Objects.hashCode(getUid());
    }

    @Override public String toString() {
        return "LuaPlayer{uid=%d, name='%s', lv=%d}".formatted(uid, name, lv);
    }
}
