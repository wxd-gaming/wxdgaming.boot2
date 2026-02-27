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
public class LuaMonster extends LuaData {

    public Long uid;
    public int cfgId;
    public String name;
    public int lv = 99;

    public LuaMonster() {
        this.setOpenHeart(true);
    }

    public LuaMonster(Long uid, int cfgId, String name) {
        this.uid = uid;
        this.cfgId = cfgId;
        this.name = name;
        this.setOpenHeart(true);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        LuaMonster that = (LuaMonster) o;
        return Objects.equals(getUid(), that.getUid());
    }

    @Override public int hashCode() {
        return Objects.hashCode(getUid());
    }

    @Override public String toString() {
        return "LuaMonster{uid=%d, cfgId=%d, name='%s', lv=%d}".formatted(uid, cfgId, name, lv);
    }
}
