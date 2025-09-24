package wxdgaming.game.login.bean.global;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.common.bean.global.AbstractGlobalData;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器名字全服数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-08 14:19
 **/
@Getter
@Setter
public class ServerShowNameGlobalData extends AbstractGlobalData {

    /**
     * 服务器冠名
     * <p>key: 服务器id
     * <p>value: 冠名信息
     */
    private ConcurrentHashMap<Integer, ServerShowName> serverNameMap = new ConcurrentHashMap<>();

    public String showName(int sid, String name) {
        ServerShowName serverShowName = serverNameMap.get(sid);
        if (serverShowName != null && System.currentTimeMillis() < serverShowName.getExpireTime()) {
            return serverShowName.getName();
        }
        return name;
    }

}
