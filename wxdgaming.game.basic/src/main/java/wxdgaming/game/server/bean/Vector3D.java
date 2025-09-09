package wxdgaming.game.server.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 三维坐标
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-15 17:26
 **/
@Getter
@Setter
public class Vector3D extends ObjectBase {

    private int x;
    private int y;
    private int z;

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Vector3D vector3D = (Vector3D) o;
        return getX() == vector3D.getX() && getY() == vector3D.getY() && getZ() == vector3D.getZ();
    }

    @Override public int hashCode() {
        int result = getX();
        result = 31 * result + getY();
        result = 31 * result + getZ();
        return result;
    }
}
